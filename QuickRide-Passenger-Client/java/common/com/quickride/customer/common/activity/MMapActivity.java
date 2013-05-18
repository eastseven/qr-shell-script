package com.quickride.customer.common.activity;

import ac.mm.android.app.ExpandApplication;
import android.os.Bundle;

import com.mapabc.mapapi.map.MapActivity;
import com.quickride.customer.R;
import com.quickride.customer.common.domain.MenuExitable;
import com.quickride.customer.common.util.MNotificationUtil;
import com.quickride.customer.trans.view.MenuButton;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-31
 * @version 1.0
 */

public abstract class MMapActivity extends MapActivity implements MenuExitable {
	protected MNotificationUtil notificationUtil;

	protected MenuButton menuButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ExpandApplication) getApplication()).pushActivity(this);

		notificationUtil = new MNotificationUtil(this);
	}

	// @Override
	// protected void onUserLeaveHint() {
	// super.onUserLeaveHint();
	//
	// notificationUtil.notifyRunning();
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();

		((ExpandApplication) getApplication()).popActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		menuButton = (MenuButton) findViewById(R.id.menu);

		notificationUtil.notifyRunning();
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();

		notificationUtil.cancelCarArriveNotification();
	}

	@Override
	public void onBackPressed() {
		if (!menuButton.isShowing()) {
			exit(null);
		} else {
			menuButton.dismiss();
		}
	}
}
