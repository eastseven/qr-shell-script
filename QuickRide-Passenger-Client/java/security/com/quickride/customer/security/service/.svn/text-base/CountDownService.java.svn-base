package com.quickride.customer.security.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-4
 * @version 1.0
 */

public class CountDownService extends Service {
	public static final int COUNT_DOWN = 90;

	private boolean threadDisable;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int countDown = COUNT_DOWN; countDown > 0 && !threadDisable;) {
						countDown--;

						Intent intent = new Intent();
						intent.setAction(CountDownService.class.getName());
						intent.putExtra("countDown", countDown);

						sendBroadcast(intent);

						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					stopSelfResult(startId);
				}
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		threadDisable = true;
	}
}
