package com.quickride.customer.trans.activity;

import ac.mm.android.activity.BaseActivity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import com.quickride.customer.R;
import com.quickride.customer.common.service.PassengerTcpService;
import com.quickride.customer.common.util.MNotificationUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-2-29
 * @version 1.0
 */

public class GetOnCarNotificationActivity extends BaseActivity {
	private PassengerTcpService tcpService;

	private ServiceConnection serviceConnection;

	private MNotificationUtil mNotificationUtil;

	private AlertDialog alert;

	private class MServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			tcpService = (PassengerTcpService) ((PassengerTcpService.LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			tcpService = null;
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setContent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mNotificationUtil = new MNotificationUtil(this);

		serviceConnection = new MServiceConnection();
		bindService(new Intent(this, PassengerTcpService.class), serviceConnection, BIND_AUTO_CREATE);

		setContent(getIntent());
	}

	private void setContent(final Intent intent) {
		if (null != alert) {
			alert.dismiss();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.car_arrive)
				.setIcon(R.drawable.ic_menu_more).setMessage(intent.getStringExtra("content"))
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (null != tcpService) {
							tcpService.respondNofityRide(intent.getIntExtra("seqNo", 0));

							mNotificationUtil.cancelCarArriveNotification();

							startActivity(new Intent().setClass(GetOnCarNotificationActivity.this,
									CheckRouteWithMapAbcActivity.class).setFlags(
									Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));

							finish();

							dialog.dismiss();
						}
					}
				}).setCancelable(false);

		alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.show();

		// LinearLayout linearLayout = new LinearLayout(this);
		// linearLayout.setBackgroundColor(Color.WHITE);
		// linearLayout.setOrientation(LinearLayout.VERTICAL);
		// linearLayout.setLayoutParams(new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.FILL_PARENT));
		//
		// TextView contentTv = new TextView(this);
		// contentTv.setTextColor(Color.BLACK);
		// contentTv.setText("\n" + intent.getStringExtra("content") +
		// "\n");
		// contentTv.setTextSize(20);
		//
		// linearLayout.addView(contentTv);
		//
		// Button okBtn = new Button(this);
		// okBtn.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT, 1));
		// okBtn.setText(R.string.confirm);
		// okBtn.setSingleLine();
		// okBtn.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (null != tcpService) {
		// tcpService.respondNofityRide(intent.getIntExtra("seqNo", 0));
		//
		// new
		// MNotificationUtil(GetOnCarNotificationActivity.this).cancelCarArriveNotification();
		//
		// startActivity(new
		// Intent().setClass(GetOnCarNotificationActivity.this,
		// CheckRouteActivity.class)
		// .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
		//
		// finish();
		// }
		// }
		// });
		//
		// linearLayout.addView(okBtn);
		//
		// setContentView(linearLayout);
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mNotificationUtil.cancelCarArriveNotification();

		unbindService(serviceConnection);

		if (null != alert) {
			alert.dismiss();
		}
	}
}
