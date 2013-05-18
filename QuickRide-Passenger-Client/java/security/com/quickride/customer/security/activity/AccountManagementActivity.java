package com.quickride.customer.security.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.security.util.AutoLoginUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-11
 * @version 1.0
 */

public class AccountManagementActivity extends MGestureSwitchPageActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_management);

		Button modifyAccountInfo = (Button) findViewById(R.id.modify_account_info);
		modifyAccountInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountManagementActivity.this, ModifyAccountInfoActivity.class);

				startActivity(intent);
			}
		});

		Button modifyPasswordButton = (Button) findViewById(R.id.modify_password);
		modifyPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountManagementActivity.this, ModifyPasswordActivity.class);

				startActivity(intent);
			}
		});

		Button modifyPhoneNumberButton = (Button) findViewById(R.id.modify_phone_number);
		modifyPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountManagementActivity.this, ModifyPhoneNumberActivity.class);

				startActivity(intent);
			}
		});

		Button myInviteButton = (Button) findViewById(R.id.invite_no);
		myInviteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountManagementActivity.this, MyInviteActivity.class);

				startActivity(intent);
			}
		});

		final CheckBox aotuLoginCheckBox = (CheckBox) findViewById(R.id.auto_login);

		if (AutoLoginUtil.isAutoLogin(this)) {
			aotuLoginCheckBox.setChecked(true);
		} else {
			aotuLoginCheckBox.setChecked(false);
		}

		aotuLoginCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					AutoLoginUtil.enableAutoLogin(AccountManagementActivity.this);

					Toast.makeText(AccountManagementActivity.this,
							getString(R.string.enable) + getString(R.string.auto_login), Toast.LENGTH_SHORT).show();
				} else {
					AutoLoginUtil.unableAutoLogin(AccountManagementActivity.this);

					Toast.makeText(AccountManagementActivity.this,
							getString(R.string.cancel) + getString(R.string.auto_login), Toast.LENGTH_SHORT).show();
				}
			}
		});

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

			((TextView) findViewById(R.id.version)).setText("版本:   " + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
