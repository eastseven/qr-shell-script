package com.quickride.customer.common.util;

import ac.mm.android.util.communication.NotificationUtil;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.quickride.customer.R;
import com.quickride.customer.trans.activity.CheckRouteWithMapAbcActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-2-1
 * @version 1.0
 */

public class MNotificationUtil extends NotificationUtil {
	public static final int CAR_ARRIVE_NOTIFICATION = 1000;

	public static volatile boolean isNotifyCarArrive;

	public MNotificationUtil(Context context) {
		super(context);
	}

	public void notifyRunning() {
		notifyRunning(((Activity) context).getIntent(), R.drawable.icon,
				context.getString(R.string.app_name) + context.getString(R.string.running),
				context.getString(R.string.app_name), ((Activity) context).getTitle());
	}

	public synchronized void notifyCarArrive() {
		Notification notification = new Notification();
		notification.icon = R.drawable.car;
		notification.tickerText = context.getString(R.string.car_arrive);
		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "/" + R.raw.car_arrive);
		notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;

		Intent intent = new Intent();
		intent.setClass(context, CheckRouteWithMapAbcActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		PendingIntent contentIntent = PendingIntent.getActivity(context, R.string.app_name, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, context.getString(R.string.app_name),
				context.getString(R.string.car_arrive), contentIntent);

		notificationManager.notify(CAR_ARRIVE_NOTIFICATION, notification);

		isNotifyCarArrive = true;
	}

	public synchronized void cancelCarArriveNotification() {
		if (isNotifyCarArrive) {
			notificationManager.cancel(CAR_ARRIVE_NOTIFICATION);

			isNotifyCarArrive = false;
		}
	}
}
