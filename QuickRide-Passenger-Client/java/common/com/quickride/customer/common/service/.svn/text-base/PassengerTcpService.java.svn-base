package com.quickride.customer.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.coder.impl.ByteUtils;
import ac.mm.android.util.remote.tcp.TcpService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.quickride.customer.R;
import com.quickride.customer.common.domain.AppMessage;
import com.quickride.customer.common.domain.BusinessCode;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.util.MNotificationUtil;
import com.quickride.customer.security.activity.LoginActivity;
import com.quickride.customer.trans.activity.GetOffCarNotificationActivity;
import com.quickride.customer.trans.activity.GetOnCarNotificationActivity;
import com.quickride.customer.trans.activity.LoadCustomerNotificationActivity;
import com.quickride.customer.trans.activity.SentCarNotificationActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-3-8
 * @version 1.0
 */

public class PassengerTcpService extends TcpService {
	public static String Tag = "PassengerTcpService";

	public static final String SESSION_ID = "SESSION_ID";

	public static final String SEQ_NO = "SEQ_NO";

	private MNotificationUtil notificationUtil;

	@Override
	public void onCreate() {
		TCP_SERVER_IP = getString(R.string.tcp_ip);
		TCP_SERVER_PORT = Integer.valueOf(getString(R.string.tcp_port));
		PULSE_INTERVAL = Integer.valueOf(getString(R.string.pulse_interval));
		// RECONNECT_INTERVAL = PULSE_INTERVAL;

		notificationUtil = new MNotificationUtil(this);

		super.onCreate();
	}

	@Override
	protected int receiveServerMessage() throws IOException {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();

		while (byteArrayOutput.size() < 2 && !serverDestroy) {
			int data = serverReader.read();

			if (data != -1) {
				byteArrayOutput.write(data);
			} else {
				return data;
			}
		}

		int totalLength = ByteUtils.binaryToInt(byteArrayOutput.toByteArray());
		Log.d(Tag, "receive totalLength=" + totalLength);
		while (byteArrayOutput.size() < totalLength && !serverDestroy) {
			int data = serverReader.read();

			if (data != -1) {
				byteArrayOutput.write(data);
			} else {
				Log.d(Tag, "receive " + data);

				return data;
			}
		}

		handleServerMessage(new AppMessage(byteArrayOutput.toByteArray()));

		return 0;
	}

	private void handleServerMessage(AppMessage appMessage) {
		String statusCode = appMessage.getStatusCode();

		if (StatusCode.USER_NOT_LOGIN.equals(statusCode)) {
			startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK));

			return;
		}

		BusinessCode businessCode = BusinessCode.codeOf(appMessage.getCmdCode());

		String body = appMessage.getBody();

		Log.d(Tag, "receive: " + appMessage.getRawHex() + ", CmdCode=" + businessCode + ", statusCode=" + statusCode);

		switch (businessCode) {
		case MO_CONNECT_CUSTOMER_RESP:
			if (StatusCode.SUCCESS.equals(statusCode)) {
				Log.d(Tag, "建立TCP链接成功！");
			} else {
				connectServer();
			}
			break;

		case MT_UPDATE_ROUTE_RESP:
			try {
				double carX = Double.valueOf(new String(ByteUtils.hexStringToBytes(body.substring(0, 32).trim()),
						"utf-8"));

				double carY = Double.valueOf(new String(ByteUtils.hexStringToBytes(body.substring(32, 64).trim()),
						"utf-8"));

				Log.d(Tag, "carX=" + carX + ", carY=" + carY);

				Intent intent = new Intent();
				intent.setAction(getClass().getName());
				intent.putExtra("carX", carX);
				intent.putExtra("carY", carY);

				sendBroadcast(intent);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;

		case MT_NOTIFY_DISPATCH_RESP:
			try {
				int messageLength = Integer.parseInt(body.substring(0, 4).trim(), 16) * 2;

				String content = new String(ByteUtils.hexStringToBytes(body.substring(4, messageLength + 4)), "utf-8")
						.trim();

				Intent newIntent = new Intent();
				newIntent.setClass(this, SentCarNotificationActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				newIntent.putExtra("content", content);
				newIntent.putExtra("seqNo", appMessage.getSeqNo());

				startActivity(newIntent);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			break;

		case MT_NOTIFY_RIDE_RESP:
			try {
				int messageLength = Integer.parseInt(body.substring(0, 4).trim(), 16) * 2;

				String content = new String(ByteUtils.hexStringToBytes(body.substring(4, messageLength + 4)), "utf-8")
						.trim();

				notificationUtil.notifyCarArrive();

				Intent newIntent = new Intent();
				newIntent.setClass(this, GetOnCarNotificationActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				newIntent.putExtra("content", content);
				newIntent.putExtra("seqNo", appMessage.getSeqNo());

				startActivity(newIntent);

				Intent intent = new Intent();
				intent.setAction(getClass().getName());
				intent.putExtra("arrived", true);

				sendBroadcast(intent);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			break;

		case MT_LOAD_CUSTOMER_RESP:
			try {
				int messageLength = Integer.parseInt(body.substring(0, 4).trim(), 16) * 2;

				String content = new String(ByteUtils.hexStringToBytes(body.substring(4, messageLength + 4)), "utf-8")
						.trim();

				Intent newIntent = new Intent();
				newIntent.setClass(this, LoadCustomerNotificationActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				newIntent.putExtra("content", content);

				startActivity(newIntent);

				Intent intent = new Intent();
				intent.setAction(getClass().getName());
				intent.putExtra("loadCustomer", true);

				sendBroadcast(intent);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			break;

		case MT_UNLOAD_CUSTOMER_RESP:
			try {
				String orderNo = new String(ByteUtils.hexStringToBytes(body.substring(0, 40)), "utf-8").trim();

				int messageLength = Integer.parseInt(body.substring(40, 44).trim(), 16) * 2;

				String content = new String(ByteUtils.hexStringToBytes(body.substring(44, messageLength + 44)), "utf-8")
						.trim();

				Intent newIntent = new Intent();
				newIntent.setClass(this, GetOffCarNotificationActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				newIntent.putExtra("orderNo", orderNo);
				newIntent.putExtra("content", content);

				startActivity(newIntent);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	private void connectServer() {
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MO_CONNECT_CUSTOMER_REQ, getSeqNo(), new byte[0])
					.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respondNofityRide(int seqNo) {
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MT_NOTIFY_RIDE_REQ, getSeqNo(), new byte[0]).toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respondNofityGetOffCar(int seqNo) {
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MT_UNLOAD_CUSTOMER_REQ, getSeqNo(), new byte[0])
					.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respondNofityLoad(int seqNo) {
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MT_LOAD_CUSTOMER_REQ, getSeqNo(), new byte[0])
					.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respondNofityDispatch(int seqNo) {
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MT_NOTIFY_DISPATCH_REQ, getSeqNo(), new byte[0])
					.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void connectedHandle() {
		connectServer();
	}

	@Override
	protected void sendPulse() {
		if (((ExpandApplication) getApplication()).isExitedApp()) {
			stopSelf();

			return;
		}
		try {
			sendMessageToServer(new AppMessage(BusinessCode.MO_PULSE_CUSTOMER_REQ, getSeqNo(), new byte[0])
					.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized int getSeqNo() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		int seqNo = sharedPreferences.getInt(SEQ_NO, 1);

		Editor editor = sharedPreferences.edit();
		editor.putInt(SEQ_NO, seqNo + 1);
		editor.commit();

		return seqNo;
	}

	private SharedPreferences getSharedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(getApplicationInfo().name, Context.MODE_PRIVATE);
		return sharedPreferences;
	}
}
