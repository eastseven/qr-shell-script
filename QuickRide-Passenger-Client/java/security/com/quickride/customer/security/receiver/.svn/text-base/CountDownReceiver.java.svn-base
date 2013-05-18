package com.quickride.customer.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.quickride.customer.R;
import com.quickride.customer.security.service.CountDownService;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-11
 * @version 1.0
 */

public class CountDownReceiver extends BroadcastReceiver {
	private Button getSmsCodeButton;

	public CountDownReceiver(Button getSmsCodeButton) {
		this.getSmsCodeButton = getSmsCodeButton;
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent.getAction().equals(CountDownService.class.getName())) {
			getSmsCodeButton.setEnabled(false);

			int countDown = intent.getIntExtra("countDown", 0);

			getSmsCodeButton.setText(countDown < 10 ? "0" + countDown : "" + countDown);

			if (countDown == 0) {
				getSmsCodeButton.setText(R.string.get_sms_code);
				getSmsCodeButton.setEnabled(true);
			}
		}
	}
}
