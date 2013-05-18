package com.quickride.customer.payment.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.download.image.ImageDownloader;
import ac.mm.android.view.ScrollPagingListView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * @date 2012-5-28
 * @version 1.0
 */

public class ExchangeCouponsActivity extends MGestureSwitchPageActivity {
	public static String Tag = "ExchangeCouponsActivity";

	private ImageDownloader imageDownloader;

	private ScrollPagingListView couponsListView;

	private EndpointClient endpointClient;

	private ScrollPagingListView.Adapter<Map<String, ?>> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_coupons);

		imageDownloader = ((ExpandApplication) getApplication()).getImageDownloader();

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

		couponsListView = (ScrollPagingListView) findViewById(R.id.coupons_list);
		TextView blankPageAlertView = new TextView(this);
		blankPageAlertView.setText("当前无可兑换优惠券");
		blankPageAlertView.setTextSize(18);
		blankPageAlertView.setGravity(Gravity.CENTER);
		blankPageAlertView.setTextColor(Color.BLACK);
		couponsListView.setBlankPageAlertFooterView(blankPageAlertView);
		couponsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final Map<String, ?> coupons = (Map<String, ?>) view.getTag();

				new AlertDialog.Builder(ExchangeCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
						.setTitle(R.string.point_exchange_coupons)
						.setMessage("确定用" + coupons.get("point") + "积分兑换《" + coupons.get("name") + "》？")
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								exchangeCoupons(coupons);
							}
						}).setNegativeButton(R.string.cancel, null).show();
			}
		});

		adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {
				Map<String, String> listMyCouponsInfo = new HashMap<String, String>();
				listMyCouponsInfo.put("pageNo", String.valueOf(pageNo));
				listMyCouponsInfo.put("pageSize", "10");

				Map<String, ?> result = endpointClient.getAllCoupons(listMyCouponsInfo);

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					return null;
				}

				int totalPage = (Integer) result.get("totalPage");

				if (pageNo >= totalPage) {
					couponsListView.notifyIsLastPage();
				}

				List<Map<String, ?>> couponsList = (List<Map<String, ?>>) result.get("result");

				Log.d(Tag, "totalPage=" + totalPage + ", nextPageCount=" + pageNo + ", couponsList=" + couponsList);

				return couponsList;
			}

			@Override
			protected View getView(int arg0, final Map<String, ?> coupons, View arg2, ViewGroup arg3) {
				LinearLayout couponsItem = (LinearLayout) getLayoutInflater().inflate(R.layout.exchange_coupons_item,
						null);

				couponsItem.setTag(coupons);

				imageDownloader.download((String) coupons.get("logoUrl"),
						((ImageView) couponsItem.findViewById(R.id.logo)));

				TextView nameTextView = (TextView) couponsItem.findViewById(R.id.name);
				nameTextView.setText((String) coupons.get("name"));

				TextView infoTextView = (TextView) couponsItem.findViewById(R.id.info);
				infoTextView.setText((String) coupons.get("description"));

				TextView timeTextView = (TextView) couponsItem.findViewById(R.id.time);
				timeTextView.setText("开始时间：" + coupons.get("beginDate"));

				TextView endTimeTextView = (TextView) couponsItem.findViewById(R.id.end_time);
				endTimeTextView.setText("过期时间：" + coupons.get("endDate"));

				TextView remainderCountTextView = (TextView) couponsItem.findViewById(R.id.remainder_count);
				remainderCountTextView.setText("剩余张数：" + coupons.get("remainCount"));

				TextView countTextView = (TextView) couponsItem.findViewById(R.id.count);
				countTextView.setText("您已拥有张数：" + coupons.get("takeCount"));

				TextView useDescTextView = (TextView) couponsItem.findViewById(R.id.content);
				useDescTextView.setText("兑换所需积分：" + coupons.get("point"));

				TextView takeDescTextView = (TextView) couponsItem.findViewById(R.id.description);
				takeDescTextView.setText("使用说明：" + coupons.get("useDesc"));

				return couponsItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		couponsListView.setAdapter(adapter);
	}

	private void exchangeCoupons(final Map<String, ?> coupons) {
		final ProgressDialog progressDialog = ProgressDialog.show(ExchangeCouponsActivity.this, null,
				getString(R.string.waitting), true, true);

		new EndpointClient(ExchangeCouponsActivity.this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("couponId", coupons.get("couponId").toString());

				return exchangeCoupon(info);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result) {
					new AlertDialog.Builder(ExchangeCouponsActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									ExchangeCouponsActivity.this.exchangeCoupons(coupons);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(ExchangeCouponsActivity.this, R.string.request_fail,
											Toast.LENGTH_LONG).show();
								}
							}).setCancelable(false).show();

					return;
				}

				if ("1904".equals((String) result.get("statusCode"))) {
					Toast.makeText(ExchangeCouponsActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				if ("1906".equals((String) result.get("statusCode"))) {
					Toast.makeText(ExchangeCouponsActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				if ("1908".equals((String) result.get("statusCode"))) {
					Toast.makeText(ExchangeCouponsActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					Toast.makeText(ExchangeCouponsActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				Toast.makeText(ExchangeCouponsActivity.this, "兑换成功！", Toast.LENGTH_SHORT).show();

				finish();
			}
		}.execute();
	}
}
