package com.quickride.customer.common.activity;

import ac.mm.android.app.ExpandApplication;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.security.activity.LoginActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-8-17
 * @version 1.0
 */

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExpandApplication application = (ExpandApplication) getApplication();

		application.setExitedApp(false);
		application.setDebug(false);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

			((TextView) findViewById(R.id.version)).setText("版本:   " + packageInfo.versionName + " ");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));

				finish();
			}
		}, 3000);
	}

	@Override
	public void onBackPressed() {
		return;
	}
}
