package com.quickride.customer.security.activity;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.util.receiver.SmsReceiver;
import ac.mm.android.util.security.Validator;
import ac.mm.android.view.MProgressDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.manager.GetVerificationSmsBusinessContext;
import com.quickride.customer.security.manager.GetVerificationSmsManager;
import com.quickride.customer.security.receiver.CountDownReceiver;
import com.quickride.customer.security.receiver.VerificationSmsReceiver;
import com.quickride.customer.security.service.CountDownService;
import com.quickride.customer.security.util.ValidatorUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-13
 * @version 1.0
 */

public class ModifyPhoneNumberActivity extends MGestureSwitchPageActivity {
	private TextView verificationCodeText;
	private EditText phoneEditText;
	private EditText verificationCodeEditText;
	private Button getVerificationSmsButton;

	private VerificationSmsReceiver verificationSmsReceiver;
	private CountDownReceiver countDownReceiver;

	private Validator validator;

	private ProgressDialog progressDialog;

	private GetVerificationSmsManager getVerificationSmsManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_phone_number);

		progressDialog = MProgressDialog.show(this, null, getString(R.string.loading), true, true,
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						progressDialog.dismiss();

						finish();
					}
				});

		validator = new Validator();

		phoneEditText = (EditText) findViewById(R.id.phone);
		verificationCodeEditText = (EditText) findViewById(R.id.verification_code);
		getVerificationSmsButton = (Button) findViewById(R.id.get_verification_code);
		verificationCodeText = (TextView) findViewById(R.id.verification_code_text);

		registerReceiver(countDownReceiver = new CountDownReceiver(getVerificationSmsButton), new IntentFilter(
				CountDownService.class.getName()));

		IntentFilter filter = new IntentFilter(SmsReceiver.ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(verificationSmsReceiver = new VerificationSmsReceiver(this, verificationCodeText), filter);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				getVerificationSmsButton.setVisibility(View.VISIBLE);
			}
		}, 1000);

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getUser();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result) {
					Toast.makeText(ModifyPhoneNumberActivity.this, getString(R.string.loading_fail), Toast.LENGTH_SHORT)
							.show();

					progressDialog.dismiss();

					finish();

					return;
				}

				Boolean success = (Boolean) result.get("success");

				if (true == success) {
					phoneEditText.setText((String) ((Map<String, ?>) result.get("message")).get("mobile"));

					progressDialog.dismiss();
				} else {
					Toast.makeText(ModifyPhoneNumberActivity.this, (String) result.get("message"), Toast.LENGTH_LONG)
							.show();

					progressDialog.dismiss();

					finish();
				}
			}
		}.execute();

		getVerificationSmsManager = new GetVerificationSmsManager();

		getVerificationSmsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ValidatorUtil.validatePhoneNumber(ModifyPhoneNumberActivity.this, phoneEditText, validator)) {
					return;
				}

				GetVerificationSmsBusinessContext getVerificationSmsBusinessContext = new GetVerificationSmsBusinessContext();
				getVerificationSmsBusinessContext.setContext(ModifyPhoneNumberActivity.this);
				getVerificationSmsBusinessContext.setGetVerificationSmsButton(getVerificationSmsButton);
				getVerificationSmsBusinessContext.setPhoneNumber(phoneEditText.getText().toString());

				getVerificationSmsManager.execute(getVerificationSmsBusinessContext);
			}
		});

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
				if (!validatePhoneNumber()) {
					return;
				}

				confirm.setEnabled(false);

				progressDialog = ProgressDialog.show(ModifyPhoneNumberActivity.this, null,
						getString(R.string.updating), true, true);

				new EndpointClient(ModifyPhoneNumberActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> registInfo = new HashMap<String, String>();
						registInfo.put("mobile", phoneEditText.getText().toString());
						registInfo.put("registCode", verificationCodeEditText.getText().toString());

						return updateUser(registInfo);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						confirm.setEnabled(true);

						if (null == result) {
							progressDialog.dismiss();

							Toast.makeText(ModifyPhoneNumberActivity.this, R.string.modify_fail, Toast.LENGTH_LONG)
									.show();

							return;
						}

						Boolean success = (Boolean) result.get("success");

						if (true == success) {
							Toast.makeText(ModifyPhoneNumberActivity.this, R.string.modify_success, Toast.LENGTH_LONG)
									.show();

							progressDialog.dismiss();

							finish();
						} else {
							progressDialog.dismiss();

							Toast.makeText(ModifyPhoneNumberActivity.this,
									getString(R.string.modify_fail) + result.get("message"), Toast.LENGTH_LONG).show();
						}
					}
				}.execute();
			}
		});
	}

	private boolean validatePhoneNumber() {
		if (!ValidatorUtil.validatePhoneNumber(this, phoneEditText, validator)) {
			return false;
		}

		if (null == verificationCodeEditText.getText().toString()
				|| "".equals(verificationCodeEditText.getText().toString().trim())) {
			Toast.makeText(this, R.string.verification_code_hint, Toast.LENGTH_LONG).show();

			verificationCodeEditText.setError(getString(R.string.verification_code_hint));

			return false;
		}

		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}

		unregisterReceiver(verificationSmsReceiver);
		unregisterReceiver(countDownReceiver);
	}
}
