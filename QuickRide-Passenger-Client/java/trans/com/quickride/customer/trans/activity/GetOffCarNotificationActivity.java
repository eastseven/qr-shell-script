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
import com.quickride.customer.report.activity.MyOrderDetailActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-2-29
 * @version 1.0
 */

public class GetOffCarNotificationActivity extends BaseActivity {
	private PassengerTcpService tcpService;

	private ServiceConnection serviceConnection;

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

		serviceConnection = new MServiceConnection();
		bindService(new Intent(this, PassengerTcpService.class), serviceConnection, BIND_AUTO_CREATE);

		setContent(getIntent());
	}

	private void setContent(final Intent intent) {
		if (null != alert) {
			alert.dismiss();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("感谢乘坐").setIcon(R.drawable.ic_menu_more)
				.setMessage(intent.getStringExtra("content"))
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (null != tcpService) {
							tcpService.respondNofityGetOffCar(intent.getIntExtra("seqNo", 0));

							startActivity(new Intent(intent).setClass(GetOffCarNotificationActivity.this,
									MyOrderDetailActivity.class).putExtra("return_rent_main", true));

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
		// contentTv.setGravity(Gravity.CENTER);
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
		// tcpService.respondNofityGetOffCar(intent.getIntExtra("seqNo",
		// 0));
		//
		// startActivity(new
		// Intent(intent).setClass(GetOffCarNotificationActivity.this,
		// MyOrderDetailActivity.class).putExtra("return_rent_main", true));
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

		unbindService(serviceConnection);

		if (null != alert) {
			alert.dismiss();
		}
	}
}