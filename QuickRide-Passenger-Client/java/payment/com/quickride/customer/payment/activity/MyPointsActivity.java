package com.quickride.customer.payment.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.view.ScrollPagingListView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-16
 * @version 1.0
 */

public class MyPointsActivity extends MGestureSwitchPageActivity {
	public static String Tag = "MyPointsActivity";

	private EndpointClient endpointClient;

	private ScrollPagingListView pointsListView;
	private TextView myPointTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.my_points);

		pointsListView = (ScrollPagingListView) findViewById(R.id.points_list);
		myPointTextView = (TextView) findViewById(R.id.points);

		endpointClient = new EndpointClient(this) {
			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				// TODO Auto-generated method stub
			}

			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		RelativeLayout exchangeCoupons = (RelativeLayout) findViewById(R.id.get_coupons);
		exchangeCoupons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MyPointsActivity.this, ExchangeCouponsActivity.class));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		myPointTextView.setText(R.string.loading);

		getMyPoints();

		ScrollPagingListView.Adapter<Map<String, ?>> adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("pageNo", String.valueOf(pageNo));
				info.put("pageSize", "10");
				info.put("page_orderBy", "tradeTime_desc");

				Map<String, ?> result = endpointClient.getPointsLog(info);

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					return null;
				}

				int totalPage = (Integer) result.get("totalPage");

				if (pageNo >= totalPage) {
					pointsListView.notifyIsLastPage();
				}

				List<Map<String, ?>> pointsList = (List<Map<String, ?>>) result.get("result");

				Log.d(Tag, "totalPage=" + totalPage + ", nextPageCount=" + pageNo + ", pointsList=" + pointsList);

				return pointsList;
			}

			@Override
			protected View getView(int arg0, final Map<String, ?> pointsLog, View arg2, ViewGroup arg3) {
				LinearLayout pointsLogItem = (LinearLayout) getLayoutInflater().inflate(R.layout.points_log_item, null);

				((TextView) pointsLogItem.findViewById(R.id.time)).setText((String) pointsLog.get("tradeTime"));
				((TextView) pointsLogItem.findViewById(R.id.description))
						.setText((String) pointsLog.get("description"));

				Integer point = (Integer) pointsLog.get("point");

				if (0 < point) {
					((ImageView) pointsLogItem.findViewById(R.id.logo)).setImageResource(R.drawable.arrow_up);

					String pointString = "获得" + pointsLog.get("point") + "积分";

					SpannableString sp = new SpannableString(pointString);
					sp.setSpan(new ForegroundColorSpan(Color.RED), 2, pointString.length() - 2,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, pointString.length() - 2,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					((TextView) pointsLogItem.findViewById(R.id.content)).setText(sp);
				} else if (0 > point) {
					((ImageView) pointsLogItem.findViewById(R.id.logo)).setImageResource(R.drawable.arrow_down);

					String pointString = "减少" + Math.abs((Integer) pointsLog.get("point")) + "积分";

					SpannableString sp = new SpannableString(pointString);
					sp.setSpan(new ForegroundColorSpan(Color.RED), 2, pointString.length() - 2,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, pointString.length() - 2,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					((TextView) pointsLogItem.findViewById(R.id.content)).setText(sp);
				}

				return pointsLogItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		pointsListView.setAdapter(adapter);
	}

	private void getMyPoints() {
		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("pageNo", "1");
				info.put("pageSize", "1");

				return getPointsLog(info);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(MyPointsActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									getMyPoints();
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(MyPointsActivity.this, R.string.loading_fail, Toast.LENGTH_LONG)
											.show();
								}
							}).setCancelable(false).show();

					return;
				}

				String myPoint = getString(R.string.current_point) + result.get("currentIntegral");

				SpannableString sp = new SpannableString(myPoint);
				sp.setSpan(new ForegroundColorSpan(Color.RED), getString(R.string.current_point).length(),
						myPoint.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), getString(R.string.current_point).length(),
						myPoint.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				sp.setSpan(new AbsoluteSizeSpan(20, true), getString(R.string.current_point).length(),
						myPoint.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

				myPointTextView.setText(sp);
			}
		}.execute();
	}
}
