package com.quickride.customer.security.activity;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.util.security.Validator;
import ac.mm.android.view.MProgressDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MBaseActivity;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.util.ValidatorUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-6-19
 * @version 1.0
 */

public class AccountNonactivatedActivity extends MBaseActivity {
	private EditText emailEditText;

	private ProgressDialog progressDialog;

	private Validator validator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_nonactivated);

		progressDialog = MProgressDialog.show(this, null, getString(R.string.loading), true, true,
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						progressDialog.dismiss();
					}
				});

		validator = new Validator();

		emailEditText = (EditText) findViewById(R.id.email);

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getUser();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result) {
					return;
				}

				Boolean success = (Boolean) result.get("success");

				if (true == success) {
					emailEditText.setText((String) ((Map<String, ?>) result.get("message")).get("email"));
				}
			}
		}.execute();

		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final Button confirm = (Button) findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ValidatorUtil.validateEmail(AccountNonactivatedActivity.this, emailEditText, validator)) {
					return;
				}

				confirm.setEnabled(false);

				progressDialog = ProgressDialog.show(AccountNonactivatedActivity.this, null,
						getString(R.string.waitting), true, true);

				new EndpointClient(AccountNonactivatedActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> email = new HashMap<String, String>();
						email.put("email", emailEditText.getText().toString());

						return sendActiveEmail(email);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						confirm.setEnabled(true);

						progressDialog.dismiss();

						if (null == result) {
							Toast.makeText(AccountNonactivatedActivity.this, R.string.register_fail, Toast.LENGTH_LONG)
									.show();

							return;
						}

						Boolean success = (Boolean) result.get("success");

						if (true == success) {
							Toast.makeText(AccountNonactivatedActivity.this,
									getString(R.string.request_success) + " 激活邮件已发往您的邮箱，请查收", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(AccountNonactivatedActivity.this, (String) result.get("message"),
									Toast.LENGTH_LONG).show();
						}
					}
				}.execute();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}
}
