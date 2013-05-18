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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.activity.WebViewActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.activity.AccountNonactivatedActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-18
 * @version 1.0
 */

public class GetCouponsActivity extends MGestureSwitchPageActivity {
	public static String Tag = "GetCouponsActivity";

	private ImageDownloader imageDownloader;

	private ScrollPagingListView couponsListView;

	private EndpointClient endpointClient;

	private ScrollPagingListView.Adapter<Map<String, ?>> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_coupons);

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
		blankPageAlertView.setText("当前没有可领取优惠券信息");
		blankPageAlertView.setTextSize(18);
		blankPageAlertView.setGravity(Gravity.CENTER);
		blankPageAlertView.setTextColor(Color.BLACK);
		couponsListView.setBlankPageAlertFooterView(blankPageAlertView);
	}

	private void getCoupons(final Map<String, ?> coupons) {
		final ProgressDialog progressDialog = ProgressDialog.show(GetCouponsActivity.this, null,
				getString(R.string.waitting), true, true);

		new EndpointClient(GetCouponsActivity.this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("couponId", coupons.get("couponId").toString());

				return getCoupon(info);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result) {
					new AlertDialog.Builder(GetCouponsActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									GetCouponsActivity.this.getCoupons(coupons);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(GetCouponsActivity.this, R.string.request_fail, Toast.LENGTH_LONG)
											.show();
								}
							}).setCancelable(false).show();

					return;
				}

				if ("1412".equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(GetCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle("领取优惠券提示").setMessage("温馨提示：您的注册邮箱还没有进行验证，请您先验证邮箱后再领取优惠券。")
							.setPositiveButton("验证注册邮箱", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									startActivity(new Intent(GetCouponsActivity.this, AccountNonactivatedActivity.class));
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(GetCouponsActivity.this, (String) result.get("statusMessage"),
											Toast.LENGTH_LONG).show();
								}
							}).setCancelable(false).show();

					return;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					Toast.makeText(GetCouponsActivity.this, (String) result.get("statusMessage"), Toast.LENGTH_LONG)
							.show();

					return;
				}

				// List<Map<String, ?>> couponsList = adapter.getItemDataList();
				//
				// for (Map<String, ?> coupon : couponsList) {
				// if (coupon.get("couponId").equals(coupons.get("couponId"))) {
				// HashMap<String, Object> newCoupons = new HashMap<String,
				// Object>(coupon);
				// newCoupons.put("take", StatusCode.GET_COUPONS_GOT);
				//
				// couponsList.set(couponsList.indexOf(coupon), newCoupons);
				// }
				// }
				//
				// adapter.notifyDataSetChanged();

				new AlertDialog.Builder(GetCouponsActivity.this).setMessage("领取成功！")
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								setupCouponsListView();
							}
						}).show();
			}
		}.execute();
	}

	@Override
	protected void onStart() {
		super.onStart();

		setupCouponsListView();
	}

	private void setupCouponsListView() {
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
				LinearLayout couponsItem = (LinearLayout) getLayoutInflater().inflate(R.layout.all_coupons_item, null);

				Integer takeStatus = (Integer) coupons.get("take");

				ImageView logoImageView = (ImageView) couponsItem.findViewById(R.id.logo);

				TextView nameTextView = (TextView) couponsItem.findViewById(R.id.name);
				nameTextView.setText((String) coupons.get("name"));

				TextView infoTextView = (TextView) couponsItem.findViewById(R.id.info);
				infoTextView.setText((String) coupons.get("description"));

				TextView remainderCountTextView = (TextView) couponsItem.findViewById(R.id.remainder_count);
				remainderCountTextView.setText("剩余张数：" + coupons.get("remainCount"));

				TextView countTextView = (TextView) couponsItem.findViewById(R.id.count);
				countTextView.setText("您已拥有张数：" + coupons.get("takeCount"));

				TextView timeTextView = (TextView) couponsItem.findViewById(R.id.time);
				timeTextView.setText("开始时间：" + coupons.get("beginDate"));

				TextView endTimeTextView = (TextView) couponsItem.findViewById(R.id.end_time);
				endTimeTextView.setText("过期时间：" + coupons.get("endDate"));

				TextView useDescTextView = (TextView) couponsItem.findViewById(R.id.content);
				useDescTextView.setText("使用说明：" + coupons.get("useDesc"));

				TextView takeDescTextView = (TextView) couponsItem.findViewById(R.id.description);
				takeDescTextView.setText("领取说明：" + coupons.get("takeDesc"));

				TextView menuTextView = (TextView) couponsItem.findViewById(R.id.menu);

				if (takeStatus == StatusCode.GET_COUPONS_ENABLE) {
					imageDownloader.download((String) coupons.get("logoUrl"), logoImageView);

					menuTextView.setText("点击领取");
					menuTextView.setVisibility(View.VISIBLE);
					menuTextView.setTextColor(getResources().getColor(R.color.greenyellow));
					couponsItem.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							new AlertDialog.Builder(GetCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
									.setTitle(R.string.get_coupons).setMessage("确认领取《" + coupons.get("name") + "》？")
									.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											getCoupons(coupons);
										}
									}).setNegativeButton(R.string.no, null).show();
						}
					});
				} else if (takeStatus == StatusCode.GET_COUPONS_GOT) {
					imageDownloader.download((String) coupons.get("logoUrl"), logoImageView);

					menuTextView.setVisibility(View.VISIBLE);
					menuTextView.setText("已达领取上限");
					menuTextView.setTextColor(getResources().getColor(R.color.littlegreen));
				} else if (takeStatus == StatusCode.GET_COUPONS_JOIN_ACTIVITY) {
					imageDownloader.download((String) coupons.get("logoUrl"), logoImageView);

					menuTextView.setVisibility(View.VISIBLE);
					menuTextView.setText("参加活动领取优惠券");
					menuTextView.setTextColor(getResources().getColor(R.color.red));
					couponsItem.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							new AlertDialog.Builder(GetCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
									.setTitle("参加活动").setMessage("立即查看活动详情？")
									.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											Intent intent = new Intent(GetCouponsActivity.this, WebViewActivity.class);
											intent.putExtra("url", (String) coupons.get("takeUrl"));
											startActivity(intent);
										}
									}).setNegativeButton(R.string.no, null).show();
						}
					});
				} else if (takeStatus == StatusCode.GET_COUPONS_LOCK) {
					logoImageView.setImageResource(R.drawable.ic_lock_lock);

					nameTextView.setTextSize(16);
				}

				return couponsItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		couponsListView.setAdapter(adapter);
	}
}
