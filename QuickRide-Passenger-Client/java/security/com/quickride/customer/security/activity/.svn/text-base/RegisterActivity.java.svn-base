package com.quickride.customer.security.activity;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.util.receiver.SmsReceiver;
import ac.mm.android.util.security.Validator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
 * ��˵����
 * 
 * @author WPM
 * @date 2011-11-17
 * @version 1.0
 */

public class RegisterActivity extends MGestureSwitchPageActivity {
	private TextView verificationCodeText;
	private EditText phoneEditText;
	private EditText emailEditText;
	private EditText nameEditText;
	private EditText userPasswordEditText;
	private EditText againPasswordEditText;
	private EditText verificationCodeEditText;
	private EditText inviteNoEditText;
	private Button getVerificationSmsButton;

	private VerificationSmsReceiver verificationSmsReceiver;
	private CountDownReceiver countDownReceiver;

	private Validator validator;

	private ProgressDialog progressDialog;

	private GetVerificationSmsManager getVerificationSmsManager;

	private CheckBox registerProvisionCheckBox;
	private TextView registerProvisionText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		validator = new Validator();

		phoneEditText = (EditText) findViewById(R.id.phone);
		emailEditText = (EditText) findViewById(R.id.email);
		nameEditText = (EditText) findViewById(R.id.name);
		userPasswordEditText = (EditText) findViewById(R.id.user_password);
		againPasswordEditText = (EditText) findViewById(R.id.again_password);
		verificationCodeEditText = (EditText) findViewById(R.id.verification_code);
		getVerificationSmsButton = (Button) findViewById(R.id.get_verification_code);
		verificationCodeText = (TextView) findViewById(R.id.verification_code_text);
		inviteNoEditText = (EditText) findViewById(R.id.invite_no);
		registerProvisionCheckBox = (CheckBox) findViewById(R.id.registerProvisionCheckBox);
		registerProvisionText = (TextView) findViewById(R.id.registerProvisionText);

		SpannableString sp = new SpannableString("服务条款");
		sp.setSpan(new URLSpan(getString(R.string.domain) + getString(R.string.get_agreement_url)), 0, 4,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		registerProvisionText.setText(sp);
		registerProvisionText.setMovementMethod(LinkMovementMethod.getInstance());

		if (!getIntent().getBooleanExtra("invite", true)) {
			inviteNoEditText.setVisibility(View.GONE);
		}

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

		getVerificationSmsManager = new GetVerificationSmsManager();

		getVerificationSmsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ValidatorUtil.validatePhoneNumber(RegisterActivity.this, phoneEditText, validator)) {
					return;
				}

				GetVerificationSmsBusinessContext getVerificationSmsBusinessContext = new GetVerificationSmsBusinessContext();
				getVerificationSmsBusinessContext.setContext(RegisterActivity.this);
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

		final Button register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!validateRegisterInfo()) {
					return;
				}

				register.setEnabled(false);

				progressDialog = ProgressDialog.show(RegisterActivity.this, null, getString(R.string.registering),
						true, true);

				new EndpointClient(RegisterActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> registInfo = new HashMap<String, String>();
						registInfo.put("email", emailEditText.getText().toString());
						registInfo.put("password", userPasswordEditText.getText().toString());
						registInfo.put("realName", nameEditText.getText().toString());
						registInfo.put("mobile", phoneEditText.getText().toString());
						registInfo.put("registCode", verificationCodeEditText.getText().toString());
						registInfo.put("inviteNo", inviteNoEditText.getText().toString());

						return regist(registInfo);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						register.setEnabled(true);

						if (null == result) {
							progressDialog.dismiss();

							Toast.makeText(RegisterActivity.this, R.string.register_fail, Toast.LENGTH_LONG).show();

							return;
						}

						Boolean success = (Boolean) result.get("success");

						if (true == success) {
							Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_LONG).show();

							new AlertDialog.Builder(RegisterActivity.this).setIcon(R.drawable.ic_menu_more)
									.setTitle(R.string.register_success).setMessage("注册成功，恭喜您成为爱订车会员！")
									.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Uri uri =
											// Uri.parse(emailEditText.getText().toString());
											// Intent intent = new
											// Intent(Intent.ACTION_VIEW, uri);
											// startActivity(intent);

											progressDialog.dismiss();

											finish();
										}
									}).setCancelable(false).show();
						} else {
							progressDialog.dismiss();

							Toast.makeText(RegisterActivity.this,
									getString(R.string.register_fail) + result.get("message"), Toast.LENGTH_LONG)
									.show();
						}
					}
				}.execute();
			}
		});
	}

	private boolean validateRegisterInfo() {
		if (null == emailEditText.getText().toString() || "".equals(emailEditText.getText().toString().trim())) {
			Toast.makeText(this, R.string.email_hint, Toast.LENGTH_SHORT).show();

			emailEditText.setError(getString(R.string.email_hint));

			return false;
		}

		if (!validator.validateEmail(emailEditText.getText().toString())) {
			Toast.makeText(this, "邮箱验证不合法", Toast.LENGTH_SHORT).show();

			emailEditText.setError("邮箱验证不合法");

			return false;
		}

		if (!ValidatorUtil.validatePhoneNumber(this, phoneEditText, validator)) {
			return false;
		}

		if (!ValidatorUtil.validateUserName(this, nameEditText, validator)) {
			return false;
		}

		if (!ValidatorUtil.validatePassword(this, userPasswordEditText, againPasswordEditText)) {
			return false;
		}

		if (null == verificationCodeEditText.getText().toString()
				|| "".equals(verificationCodeEditText.getText().toString().trim())) {
			Toast.makeText(this, R.string.verification_code_hint, Toast.LENGTH_SHORT).show();

			verificationCodeEditText.setError(getString(R.string.verification_code_hint));

			return false;
		}

		if (getIntent().getBooleanExtra("invite", true)) {
			if (null == inviteNoEditText.getText().toString()
					|| "".equals(inviteNoEditText.getText().toString().trim())) {
				Toast.makeText(this, R.string.invite_no_hint, Toast.LENGTH_SHORT).show();

				inviteNoEditText.setError(getString(R.string.invite_no_hint));

				return false;
			}
		}

		if (!registerProvisionCheckBox.isChecked()) {
			Toast.makeText(getApplication(), "请阅读并接受服务条款", Toast.LENGTH_SHORT).show();

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