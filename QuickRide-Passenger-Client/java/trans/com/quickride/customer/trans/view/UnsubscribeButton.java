package com.quickride.customer.trans.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.DistributorActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.report.activity.MyOrderDetailActivity;
import com.quickride.customer.report.activity.MyOrderListActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-6-7
 * @version 1.0
 */

public class UnsubscribeButton extends Button {
	private Activity context;

	public UnsubscribeButton(final Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = (Activity) context;

		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Map<String, Object> order = (Map<String, Object>) v.getTag();

				final ProgressDialog progressDialog = ProgressDialog.show(context, null,
						context.getString(R.string.waitting), true, true);

				new EndpointClient(context) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> info = new HashMap<String, String>();
						info.put("orderNo", (String) order.get("orderNo"));

						return getUnsubscribePrompt(info);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						progressDialog.dismiss();

						if (null == result) {
							Toast.makeText(context, R.string.request_fail, Toast.LENGTH_LONG).show();

							return;
						}

						if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
							Toast.makeText(context, (String) result.get("statusMessage"), Toast.LENGTH_LONG).show();

							return;
						}

						new AlertDialog.Builder(context).setIcon(R.drawable.alert).setTitle(R.string.unsubscribe)
								.setMessage((String) result.get("statusMessage"))
								.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										final ProgressDialog progressDialog = ProgressDialog.show(context, null,
												context.getString(R.string.waitting), true, true);

										UnsubscribeButton.this.unsubscribe(progressDialog, order);
									}
								}).setNegativeButton(R.string.cancel, null).show();
					}
				}.execute();
			}
		});
	}

	private void unsubscribe(final ProgressDialog progressDialog, final Map<String, Object> order) {
		new EndpointClient(context) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> unsubscribeInfo = new HashMap<String, String>();
				unsubscribeInfo.put("orderNo", (String) order.get("orderNo"));

				return unsubscribe(unsubscribeInfo);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result) {
					new AlertDialog.Builder(context).setIcon(R.drawable.alert).setTitle(R.string.server_busy)
							.setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									UnsubscribeButton.this.unsubscribe(progressDialog, order);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(context, R.string.unsubscribe_fail, Toast.LENGTH_LONG).show();
								}
							}).setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									Toast.makeText(context, R.string.unsubscribe_fail, Toast.LENGTH_LONG).show();
								}
							}).show();

					return;
				}

				if ("1806".equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(context).setIcon(R.drawable.alert).setTitle(R.string.unsubscribe)
							.setMessage((String) result.get("statusMessage"))
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									context.startActivity(new Intent(context, DistributorActivity.class));
								}
							}).show();

					return;
				}

				if ("1809".equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(context).setIcon(R.drawable.alert).setTitle(R.string.unsubscribe)
							.setMessage((String) result.get("statusMessage")).setPositiveButton(R.string.confirm, null)
							.show();

					return;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(context).setIcon(R.drawable.alert).setTitle(R.string.unsubscribe)
							.setMessage((String) result.get("statusMessage")).setPositiveButton(R.string.confirm, null)
							.show();

					return;
				}

				Toast.makeText(context, R.string.unsubscribe_success, Toast.LENGTH_LONG).show();

				if (context instanceof MyOrderDetailActivity) {
					Intent intent = new Intent(context, MyOrderListActivity.class);
					intent.putExtra("unsubscribe", true);
					intent.putExtra("order", (Serializable) order);

					context.setResult(Activity.RESULT_OK, intent);
				} else {
					context.startActivity(new Intent(context, DistributorActivity.class));
				}

				context.finish();
			}
		}.execute();
	}
}
