package com.quickride.customer.common.activity;

import ac.mm.android.activity.GestureSwitchPageActivity;
import android.os.Bundle;

import com.quickride.customer.common.util.MNotificationUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-31
 * @version 1.0
 */

public abstract class MGestureSwitchPageActivity extends GestureSwitchPageActivity {
	protected MNotificationUtil notificationUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		notificationUtil = new MNotificationUtil(this);
	}

	// @Override
	// protected void onUserLeaveHint() {
	// super.onUserLeaveHint();
	//
	// notificationUtil.notifyRunning();
	// }

	@Override
	protected void onResume() {
		super.onResume();

		notificationUtil.notifyRunning();
	}
}
