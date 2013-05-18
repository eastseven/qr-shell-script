package com.quickride.customer.ui.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.endpoint.EndpointClient;

public class ModifyPasswordActivity extends MGestureSwitchPageActivity {
	
private static final String tag = "QR_ModifyPasswordActivity";
	
	TextView headerTitle;
	Button backButton, nextButton;
	
	private EditText userPasswordEditText;
	private EditText userNewPasswordEditText;
	private EditText againPasswordEditText;

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_modify_password);
		Log.d(tag, getString(R.string.main_modify_password));
		
		this.initHeaderBar();
		this.initContent();
	}

	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_modify_password));
		
		this.backButton   = (Button) findViewById(R.id.main_header_layout_widget_left);

		this.backButton.setText(R.string.main_back);
		this.backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	
		Button right = (Button) findViewById(R.id.main_header_layout_widget_right);
		right.setVisibility(View.GONE);
	}
	
	void initContent() {
		userPasswordEditText = (EditText) findViewById(R.id.user_password);
		userNewPasswordEditText = (EditText) findViewById(R.id.new_password);
		againPasswordEditText = (EditText) findViewById(R.id.again_password);

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
				if (!validatePasswordInfo()) {
					return;
				}

				confirm.setEnabled(false);

				progressDialog = ProgressDialog.show(ModifyPasswordActivity.this, null, getString(R.string.updating),
						true, true);

				new EndpointClient(ModifyPasswordActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> passwordInfo = new HashMap<String, String>();
						passwordInfo.put("oldPassword", userPasswordEditText.getText().toString());
						passwordInfo.put("newPassword", userNewPasswordEditText.getText().toString());
						passwordInfo.put("reNewPassword", againPasswordEditText.getText().toString());

						return modifyPassword(passwordInfo);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						confirm.setEnabled(true);

						if (null == result) {
							progressDialog.dismiss();

							Toast.makeText(ModifyPasswordActivity.this, R.string.modify_fail, Toast.LENGTH_LONG).show();

							return;
						}

						Boolean success = (Boolean) result.get("success");

						if (true == success) {
							progressDialog.dismiss();

							Toast.makeText(ModifyPasswordActivity.this, R.string.modify_success, Toast.LENGTH_LONG)
									.show();

							finish();
						} else {
							progressDialog.dismiss();

							Toast.makeText(ModifyPasswordActivity.this,
									getString(R.string.modify_fail) + result.get("message"), Toast.LENGTH_LONG).show();
						}
					}
				}.execute();
			}
		});
	}

	private boolean validatePasswordInfo() {
		if (null == userPasswordEditText.getText().toString()
				|| "".equals(userPasswordEditText.getText().toString().trim())) {
			Toast.makeText(this, "请输入当前密码", Toast.LENGTH_LONG).show();

			userPasswordEditText.setError("请输入当前密码");

			return false;
		}

		if (null == userNewPasswordEditText.getText().toString()
				|| "".equals(userNewPasswordEditText.getText().toString().trim())) {
			Toast.makeText(this, "请输入新密码", Toast.LENGTH_LONG).show();

			userNewPasswordEditText.setError("请输入新密码");

			return false;
		}

		if (null == userNewPasswordEditText.getText().toString()
				|| userNewPasswordEditText.getText().toString().trim().length() < 6) {
			Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();

			userNewPasswordEditText.setError("密码至少6位");

			return false;
		}

		Pattern pat = Pattern.compile("[\u4E00-\u9FA5]+");
		Matcher matcher = pat.matcher(userNewPasswordEditText.getText().toString());
		if (matcher.find()) {
			Toast.makeText(this, "密码不能包含中文", Toast.LENGTH_SHORT).show();

			userNewPasswordEditText.setError("密码不能包含中文");

			return false;
		}

		if (userPasswordEditText.getText().toString().equals(userNewPasswordEditText.getText().toString())) {
			Toast.makeText(this, "新密码不能与原始密码相同", Toast.LENGTH_LONG).show();

			userNewPasswordEditText.setError("新密码不能与原始密码相同");

			return false;
		}

		if (!userNewPasswordEditText.getText().toString().equals(againPasswordEditText.getText().toString())) {
			Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_LONG).show();

			againPasswordEditText.setError("两次输入的新密码不一致");

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
	}
}
