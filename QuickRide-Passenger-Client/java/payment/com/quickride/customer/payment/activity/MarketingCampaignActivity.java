package com.quickride.customer.payment.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.download.image.ImageDownloader;
import ac.mm.android.util.graphics.ViewUtil;
import ac.mm.android.view.ScrollPagingListView;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.WebViewActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-16
 * @version 1.0
 */

public class MarketingCampaignActivity extends ActivityGroup {
	public static String Tag = "MarketingCampaignActivity";

	private ImageDownloader imageDownloader;

	private String notUseTabString = "notUseTab";
	private String usedTabString = "usedTab";
	private String expiredOrderTabString = "expiredOrderTab";

	private EndpointClient endpointClient;

	private TabHost tabHost;
	private LinearLayout container;
	private ScrollPagingListView.Adapter<Map<String, ?>> campaignListAdapter;
	private TextView pageNumberTextView;

	private List<Map<String, ?>> nowCampaigns;

	private volatile int nowCampaignsNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.marketing_campaign);

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

		container = (LinearLayout) findViewById(R.id.content);

		pageNumberTextView = (TextView) findViewById(R.id.header);

		ImageView backImageView = (ImageView) findViewById(R.id.back);
		backImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == nowCampaigns || nowCampaigns.isEmpty()) {
					return;
				}

				if (0 >= nowCampaignsNumber) {
					return;
				}

				nowCampaignsNumber--;

				switchNowCampaign(nowCampaignsNumber);
			}
		});

		ImageView nextImageView = (ImageView) findViewById(R.id.next);
		nextImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == nowCampaigns || nowCampaigns.isEmpty()) {
					return;
				}

				if (nowCampaigns.size() - 1 <= nowCampaignsNumber) {
					return;
				}

				nowCampaignsNumber++;

				switchNowCampaign(nowCampaignsNumber);
			}
		});

		setupTabHost();

		getCampaign();
	}

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(R.id.order_tabhost);
		tabHost.setup();

		TabSpec usedTab = tabHost.newTabSpec(usedTabString).setIndicator(getString(R.string.now_campaign))
				.setContent(R.id.finished_order);
		tabHost.addTab(usedTab);

		TabSpec notUseTab = tabHost.newTabSpec(notUseTabString).setIndicator(getString(R.string.advance_campaign))
				.setContent(R.id.wait_execute_order);
		tabHost.addTab(notUseTab);

		TabSpec expiredOrderTab = tabHost.newTabSpec(expiredOrderTabString)
				.setIndicator(getString(R.string.expired_campaign)).setContent(R.id.canceled_order);
		tabHost.addTab(expiredOrderTab);

		ViewUtil.textCentered(tabHost.getTabWidget());

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (notUseTabString.equals(tabId)) {
					campaignListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.wait_execute_order_list), tabId);
				} else if (expiredOrderTabString.equals(tabId)) {
					campaignListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.canceled_order_list), tabId);
				}
			}
		});
	}

	private ScrollPagingListView.Adapter<Map<String, ?>> setupOrderListViewAdapter(
			final ScrollPagingListView campaignListView, final String tabId) {
		TextView blankPageAlertView = new TextView(this);
		if (notUseTabString.equals(tabId)) {
			blankPageAlertView.setText("目前还没有预告活动，敬请期待");
		} else if (expiredOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("目前还没有往期活动");
		}

		blankPageAlertView.setTextSize(18);
		blankPageAlertView.setTextColor(Color.BLACK);
		blankPageAlertView.setGravity(Gravity.CENTER);
		campaignListView.setBlankPageAlertFooterView(blankPageAlertView);
		campaignListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int itemId, long position) {
				Map<String, ?> campaign = (Map<String, ?>) adapterView.getAdapter().getItem(itemId);

				Intent intent = new Intent(MarketingCampaignActivity.this, WebViewActivity.class);
				intent.putExtra("title", campaign.get("name").toString());
				intent.putExtra("url", campaign.get("advertUrl").toString());

				startActivity(intent);
			}
		});

		ScrollPagingListView.Adapter<Map<String, ?>> adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {

				Map<String, ?> result = null;

				Map<String, String> listCampaignsInfo = new HashMap<String, String>();
				listCampaignsInfo.put("pageNo", String.valueOf(pageNo));
				listCampaignsInfo.put("pageSize", "10");

				if (notUseTabString.equals(tabId)) {
					listCampaignsInfo.put("search_EQI_status", "0");

					result = endpointClient.getAllCampaign(listCampaignsInfo);
				} else if (expiredOrderTabString.equals(tabId)) {
					listCampaignsInfo.put("search_EQI_status", "2");

					result = endpointClient.getAllCampaign(listCampaignsInfo);
				}

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					return null;
				}

				int totalPage = (Integer) result.get("totalPage");

				if (pageNo >= totalPage) {
					campaignListView.notifyIsLastPage();
				}

				List<Map<String, ?>> couponsList = (List<Map<String, ?>>) result.get("result");

				Log.d(Tag, "totalPage=" + totalPage + ", nextPageCount=" + pageNo + ", couponsList=" + couponsList);

				return couponsList;
			}

			@Override
			protected View getView(int arg0, final Map<String, ?> campaign, View arg2, ViewGroup arg3) {
				LinearLayout couponsItem = (LinearLayout) getLayoutInflater().inflate(R.layout.campaign_item, null);

				imageDownloader.download((String) campaign.get("advertImage"),
						((ImageView) couponsItem.findViewById(R.id.logo)));

				TextView nameTextView = (TextView) couponsItem.findViewById(R.id.name);
				nameTextView.setText((String) campaign.get("name"));

				TextView timeTextView = (TextView) couponsItem.findViewById(R.id.time);
				TextView endTimeTextView = (TextView) couponsItem.findViewById(R.id.end_time);
				TextView contentTextView = (TextView) couponsItem.findViewById(R.id.content);

				timeTextView.setText("开始时间:" + campaign.get("beginDate"));
				endTimeTextView.setText("结束时间:" + campaign.get("endDate"));
				contentTextView.setText((String) campaign.get("drscription"));

				return couponsItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		campaignListView.setAdapter(adapter);

		return adapter;
	}

	private void getCampaign() {
		final ProgressDialog progressDialog = ProgressDialog.show(MarketingCampaignActivity.this, null,
				getString(R.string.loading), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("pageNo", "1");
				info.put("search_EQI_status", "1");

				return getAllCampaign(info);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(MarketingCampaignActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									getCampaign();
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(MarketingCampaignActivity.this, R.string.loading_fail,
											Toast.LENGTH_LONG).show();

									finish();
								}
							}).setCancelable(false).show();

					return;
				}

				nowCampaigns = (List<Map<String, ?>>) result.get("result");

				if (null == nowCampaigns || nowCampaigns.isEmpty()) {
					pageNumberTextView.setText("0/0");

					Toast.makeText(MarketingCampaignActivity.this, "目前还没有当前活动，敬请期待", Toast.LENGTH_LONG).show();

					return;
				}

				switchNowCampaign(0);
			}
		}.execute();
	}

	private void switchNowCampaign(int campaignNumber) {
		container.removeAllViews();
		container.setVisibility(View.INVISIBLE);

		SpannableString sp = new SpannableString(campaignNumber + 1 + "/" + nowCampaigns.size());
		sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		pageNumberTextView.setText(sp);

		String url = nowCampaigns.get(campaignNumber).get("advertUrl").toString();

		Intent intent = new Intent(MarketingCampaignActivity.this, WebViewActivity.class);
		intent.putExtra("url", url);

		// Activity 转为 View
		Window subActivity = getLocalActivityManager().startActivity(url, intent);

		// 容器添加View
		container.addView(subActivity.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		container.setVisibility(View.VISIBLE);
	}
}
