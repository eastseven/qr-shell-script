package com.quickride.customer.common.activity;

import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.quickride.customer.R;
import com.quickride.customer.common.domain.AppMessage;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.service.PassengerTcpService;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.activity.AccountNonactivatedActivity;
import com.quickride.customer.trans.activity.CheckRouteWithMapAbcActivity;
import com.quickride.customer.trans.activity.RentCarWithMapAbcActivity;
import com.quickride.customer.trans.activity.UndistributedCarActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-3-18
 * @version 1.0
 */

public class DistributorActivity extends MBaseActivity {
	private ProgressDialog progressDialog;

	private EndpointClient endpointClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String sessionId = getSharedPreferences().getString(PassengerTcpService.SESSION_ID, "");
		if (!"".equals(sessionId)) {
			AppMessage.setSESSION_ID(sessionId);
		}

		getRentStatus();
	}

	private void getRentStatus() {
		if (null != endpointClient) {
			endpointClient.cancel(true);
		}

		progressDialog = ProgressDialog.show(DistributorActivity.this, null, getString(R.string.loading), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				new AlertDialog.Builder(DistributorActivity.this).setIcon(R.drawable.alert).setTitle(R.string.loading)
						.setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								progressDialog.dismiss();

								DistributorActivity.this.getRentStatus();
							}
						}).setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								exit();
							}
						}).setCancelable(false).show();

				return;

			}
		});

		endpointClient = new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getRentStatus();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(DistributorActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									progressDialog.dismiss();

									DistributorActivity.this.getRentStatus();
								}
							}).setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									exit();
								}
							}).setCancelable(true).setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									progressDialog.dismiss();

									DistributorActivity.this.getRentStatus();
								}
							}).show();

					return;
				}

				startService(new Intent(DistributorActivity.this, PassengerTcpService.class));

				String sessionId = (String) result.get("sessionId");
				if (null != sessionId && sessionId.length() > 0) {
					updateTcpServiceSessionId(sessionId);
				}

				Integer rent = (Integer) result.get("rent");

				if (0 == rent) {
					Intent intent = new Intent(DistributorActivity.this, RentCarWithMapAbcActivity.class);

					startActivity(intent);
				} else if (1 == rent) {
					Intent intent = new Intent(DistributorActivity.this, UndistributedCarActivity.class);

					startActivity(intent);
				} else if (2 == rent) {
					Intent intent = new Intent(DistributorActivity.this, CheckRouteWithMapAbcActivity.class);

					startActivity(intent);
				} else if (3 == rent) {
					Intent intent = new Intent(DistributorActivity.this, AccountNonactivatedActivity.class);

					startActivity(intent);
				}

				finish();
			}
		};

		endpointClient.execute();
	}

	private void updateTcpServiceSessionId(String sessionId) {
		SharedPreferences sharedPreferences = getSharedPreferences();

		if (!sessionId.equals(sharedPreferences.getString(PassengerTcpService.SESSION_ID, ""))) {
			AppMessage.setSESSION_ID(sessionId);

			Editor editor = sharedPreferences.edit();
			editor.putString(PassengerTcpService.SESSION_ID, sessionId);
			editor.putInt(PassengerTcpService.SEQ_NO, 1);
			editor.commit();
		}
	}

	public void exit() {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_menu_more).setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						progressDialog.dismiss();

						new EndpointClient(DistributorActivity.this) {
							@Override
							protected Map<String, Object> doInBackground(Void... arg0) {
								return logout();
							}

							@Override
							protected void onEndpointClientPostExecute(Map<String, Object> result) {
								// TODO Auto-generated method stub
							}
						}.execute();

						((ExpandApplication) getApplication()).exitApp();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						DistributorActivity.this.getRentStatus();
					}
				}).setCancelable(false).show();
	}

	private SharedPreferences getSharedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(getApplicationInfo().name, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}
}
