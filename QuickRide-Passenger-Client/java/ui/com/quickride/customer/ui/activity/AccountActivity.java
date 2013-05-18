package com.quickride.customer.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.security.activity.LoginActivity;
import com.quickride.customer.security.util.AutoLoginUtil;

/**
 * 账户管理页面
 * 
 * @author eastseven
 * 
 */
public class AccountActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private static final String tag = "QR_AccountActivity";

	TextView headerTitle;
	Button backButton, nextButton;
	
	TextView versionTextView;
	Button modifyAccountInfoButton, modifyPasswordButton, modifyPhoneNumberButton, inviteNoButton;
	CheckBox autoLoginCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_account);
		Log.d(tag, getString(R.string.main_title_account));
		this.initHeaderBar();
		this.initContent();
	}

	void initHeaderBar() {

		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_title_account));
		
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
		this.versionTextView         = (TextView) findViewById(R.id.main_account_version);
		this.modifyAccountInfoButton = (Button)   findViewById(R.id.main_account_modify_account_info);
		this.modifyPasswordButton    = (Button)   findViewById(R.id.main_account_modify_password);
		this.modifyPhoneNumberButton = (Button)   findViewById(R.id.main_account_modify_phone_number);
		this.inviteNoButton          = (Button)   findViewById(R.id.main_account_invite_no);
		this.autoLoginCheckBox       = (CheckBox) findViewById(R.id.main_account_auto_login);

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			this.versionTextView.setText("版本:   " + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		this.modifyAccountInfoButton.setOnClickListener(this);
		this.modifyPasswordButton.setOnClickListener(this);
		this.modifyPhoneNumberButton.setOnClickListener(this);
		this.inviteNoButton.setOnClickListener(this);
		
		this.autoLoginCheckBox.setOnCheckedChangeListener(this);

		if (AutoLoginUtil.isAutoLogin(this)) {
			this.autoLoginCheckBox.setChecked(true);
		} else {
			this.autoLoginCheckBox.setChecked(false);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			AutoLoginUtil.enableAutoLogin(this);
			Toast.makeText(this, getString(R.string.enable) + getString(R.string.auto_login), Toast.LENGTH_SHORT).show();
		} else {
			AutoLoginUtil.unableAutoLogin(this);
			Toast.makeText(this, getString(R.string.cancel) + getString(R.string.auto_login), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_account_modify_account_info:
			this.modifyAccountInfo();
			break;
		case R.id.main_account_modify_password:
			this.modifyPassword();
			break;
		case R.id.main_account_modify_phone_number:
			this.modifyPhoneNumber();
			break;
		case R.id.main_account_invite_no:
			this.inviteFriends();
			break;
		default:
			break;
		}
	}
	
	void modifyAccountInfo() {
		final EditText view = new EditText(this);
		view.setText(getRealName());
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.main_modify_account_info);
		b.setView(view);
		
		b.setNegativeButton(R.string.cancel, null);
		b.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String realname = view.getText().toString();
				Log.d(tag, "修改后的realname="+realname);
				//TODO HTTP请求，保存至本地
				save(realname, LoginActivity.REAL_NAME);
			}
		});
		b.show();
	}
	
	/**
	 * 修改密码
	 */
	void modifyPassword() {
		Log.d(tag, getString(R.string.main_modify_password));
		Intent intent = new Intent(this, ModifyPasswordActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 修改电话号码
	 */
	void modifyPhoneNumber() {
		Log.d(tag, getString(R.string.main_modify_phone_number));
		Intent intent = new Intent(this, ModifyPhoneNumberActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 邀请好友
	 */
	void inviteFriends() {
		Log.d(tag, getString(R.string.main_invite_no));
		Intent intent = new Intent(this, MyInviteActivity.class);
		startActivity(intent);
	}
	
	private SharedPreferences getSharedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(getApplicationInfo().name, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	private String getRealName() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		return sharedPreferences.getString(LoginActivity.REAL_NAME, "");
	}
	
	private void save(String name, final String nameType) {
		SharedPreferences sharedPreferences = getSharedPreferences();

		Editor editor = sharedPreferences.edit();
		editor.putString(nameType, name);
		editor.commit();
	}
	
}
