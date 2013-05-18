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
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.util.ValidatorUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-11
 * @version 1.0
 */

public class ModifyAccountInfoActivity extends MGestureSwitchPageActivity {
	private EditText nameEditText;

	private ProgressDialog progressDialog;

	private Validator validator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_account_info);

		progressDialog = MProgressDialog.show(this, null, getString(R.string.loading), true, true,
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						progressDialog.dismiss();

						finish();
					}
				});

		validator = new Validator();

		nameEditText = (EditText) findViewById(R.id.name);

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getUser();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result) {
					Toast.makeText(ModifyAccountInfoActivity.this, getString(R.string.loading_fail), Toast.LENGTH_SHORT)
							.show();

					progressDialog.dismiss();

					finish();

					return;
				}

				Boolean success = (Boolean) result.get("success");

				if (true == success) {
					nameEditText.setText((String) ((Map<String, ?>) result.get("message")).get("realName"));

					progressDialog.dismiss();
				} else {
					Toast.makeText(ModifyAccountInfoActivity.this, (String) result.get("message"), Toast.LENGTH_LONG)
							.show();

					progressDialog.dismiss();

					finish();
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
				if (!ValidatorUtil.validateUserName(ModifyAccountInfoActivity.this, nameEditText, validator)) {
					return;
				}

				confirm.setEnabled(false);

				progressDialog = ProgressDialog.show(ModifyAccountInfoActivity.this, null,
						getString(R.string.updating), true, true);

				new EndpointClient(ModifyAccountInfoActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> user = new HashMap<String, String>();
						user.put("realName", nameEditText.getText().toString());

						return updateUser(user);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						confirm.setEnabled(true);

						if (null == result) {
							Toast.makeText(ModifyAccountInfoActivity.this, R.string.modify_fail, Toast.LENGTH_LONG)
									.show();

							progressDialog.dismiss();

							return;
						}

						Boolean success = (Boolean) result.get("success");

						if (true == success) {
							Toast.makeText(ModifyAccountInfoActivity.this, R.string.modify_success, Toast.LENGTH_LONG)
									.show();

							progressDialog.dismiss();

							finish();
						} else {
							progressDialog.dismiss();

							Toast.makeText(ModifyAccountInfoActivity.this,
									getString(R.string.modify_fail) + result.get("message"), Toast.LENGTH_LONG).show();
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
