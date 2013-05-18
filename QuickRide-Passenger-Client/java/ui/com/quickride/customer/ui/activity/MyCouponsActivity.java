package com.quickride.customer.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.download.image.ImageDownloader;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.util.DebugUtil;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 我的优惠券
 * 
 * @author eastseven
 *
 */
public class MyCouponsActivity extends TabActivity implements OnTabChangeListener, OnScrollListener {
	public static String tag = "QR_MyCouponsActivity";

	private EndpointClient endpointClient;
	private ImageDownloader imageDownloader;

	final String TAB_NOT_USE = "main_coupon_not_use";
	final String TAB_USEED   = "main_coupon_used";
	final String TAB_EXPIRED = "main_coupon_expired";
	
	//Header Bar
	TextView headerTitle;
	Button backButton, nextButton;
	
	//TabHost 标签
	TabHost tabHost;
	
	ExpandableListView listView;
	ExpandableListAdapter adapter;
	View loadMoreBar;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_my_coupons);
		Log.d(tag, getString(R.string.main_more_my_coupon));
		
		this.init();
		this.initHeaderBar();
		this.initTabHost();
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.initTabNotUse();
	}
	
	@Override
	public void onTabChanged(String tabId) {
		if(TAB_NOT_USE.equals(tabId)) {
			Log.d(tag, getString(R.string.main_coupon_not_use));
			this.initTabNotUse();
			
		} else if (TAB_USEED.equals(tabId)) {
			Log.d(tag, getString(R.string.main_coupon_used));
			this.initTabUsed();
			
		} else if(TAB_EXPIRED.equals(tabId)) {
			Log.d(tag, getString(R.string.main_coupon_expired));
			this.initTabExpired();
			
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		Log.d(tag, "onScroll view="+view+", firstVisibleItem"+firstVisibleItem+", visibleItemCount="+visibleItemCount+", totalItemCount="+totalItemCount);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(tag, "onScrollStateChanged view="+view+", scrollState="+scrollState);
	}
	
	void init() {
		this.imageDownloader = new ImageDownloader((ExpandApplication) getApplication());
		this.endpointClient = new EndpointClient(this) {
			
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				return null;
			}
			
			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				
			}
		};
		
		Map<String, String> info = new HashMap<String, String>();
		info.put("pageNo",   "1");
		info.put("pageSize", "10");
		Map<String, Object> result = this.endpointClient.getMyCoupons(info);
		DebugUtil.print(result, getClass());
	}
	
	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_more_my_coupon));
		
		this.backButton   = (Button) findViewById(R.id.main_header_layout_widget_left);

		this.backButton.setText(R.string.main_back);
		this.backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	
		Button right = (Button) findViewById(R.id.main_header_layout_widget_right);
		right.setVisibility(View.GONE);
	}
	
	void initTabHost() {
		this.tabHost = getTabHost();
		this.tabHost.addTab(tabHost.newTabSpec(TAB_NOT_USE).setIndicator(getString(R.string.main_coupon_not_use)).setContent(R.id.main_coupon_not_use));
		this.tabHost.addTab(tabHost.newTabSpec(TAB_USEED).setIndicator(getString(R.string.main_coupon_used)).setContent(R.id.main_coupon_used));
		this.tabHost.addTab(tabHost.newTabSpec(TAB_EXPIRED).setIndicator(getString(R.string.main_coupon_expired)).setContent(R.id.main_coupon_expired));
		
		this.tabHost.setOnTabChangedListener(this);
		this.listView = (ExpandableListView) findViewById(R.id.expandableListView1);
	}
	
	void initTabNotUse() {
		this.adapter = getTestAdapter();
		
		this.listView = (ExpandableListView) findViewById(R.id.expandableListView1);
		this.listView.setOnScrollListener(this);
		
		this.handler = new Handler();
		this.loadMoreBar = getLayoutInflater().inflate(R.layout.main_list_view_load_more_widget, null);
		final Button btn = (Button) this.loadMoreBar.findViewById(R.id.main_load_more_widget_btn);
		final ProgressBar pg = (ProgressBar) this.loadMoreBar.findViewById(R.id.main_load_more_widget_pg);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pg.setVisibility(View.VISIBLE);
				btn.setVisibility(View.GONE);
				long delayMillis = 2000;
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						//加载更多数据
						Toast.makeText(MyCouponsActivity.this, "加载更多数据", Toast.LENGTH_LONG).show();
						pg.setVisibility(View.GONE);
						btn.setVisibility(View.VISIBLE);
					}
				}, delayMillis);
			}
		});
		this.listView.addFooterView(loadMoreBar);

		//适配器和底部按钮的顺序不能反，否则底部按钮看不到
		this.listView.setAdapter(adapter);
		Log.d(tag, "listView="+listView+", adapter="+adapter);
	}
	
	void initTabUsed() {
		
	}
	
	void initTabExpired() {
		
	}
	
	SimpleExpandableListAdapter getTestAdapter() {
		int groupLayout = R.layout.main_coupon_item;
		int childLayout = R.layout.main_coupon_item_child;
		List<Map<String, Object>> groupData = new ArrayList<Map<String,Object>>();
		List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String,Object>>>();
		
		Map<String, Object> coupon1 = new HashMap<String, Object>();
		coupon1.put("name", "体验券");
		groupData.add(coupon1);
		
		List<Map<String, Object>> child1 = new ArrayList<Map<String,Object>>();
		Map<String, Object> childData1 = new HashMap<String, Object>();
		childData1.put("begin", "开始时间：\n1949-10-1 12:34:56");
		childData1.put("end", "过期时间：\n2013-10-1 12:34:56");
		childData1.put("desc", "描述信息：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		childData1.put("exp", "使用说明：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		child1.add(childData1);
		childData.add(child1);
		
		Map<String, Object> coupon2 = new HashMap<String, Object>();
		coupon2.put("name", "打折券");
		groupData.add(coupon2);
		
		List<Map<String, Object>> child2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> childData2 = new HashMap<String, Object>();
		childData2.put("begin", "开始时间：\n1949-10-1 12:34:56");
		childData2.put("end", "过期时间：\n2013-10-1 12:34:56");
		childData2.put("desc", "描述信息：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		childData2.put("exp", "使用说明：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		child2.add(childData2);
		childData.add(child2);
		
		Map<String, Object> coupon3 = new HashMap<String, Object>();
		coupon3.put("name", "代金券");
		groupData.add(coupon3);
		
		List<Map<String, Object>> child3 = new ArrayList<Map<String,Object>>();
		Map<String, Object> childData3 = new HashMap<String, Object>();
		childData3.put("begin", "开始时间：\n1949-10-1 12:34:56");
		childData3.put("end", "过期时间：\n2013-10-1 12:34:56");
		childData3.put("desc", "描述信息：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		childData3.put("exp", "使用说明：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		child3.add(childData3);
		childData.add(child3);
		
		Map<String, Object> coupon4 = new HashMap<String, Object>();
		coupon4.put("name", "BB券");
		groupData.add(coupon4);
		
		List<Map<String, Object>> child4 = new ArrayList<Map<String,Object>>();
		Map<String, Object> childData4 = new HashMap<String, Object>();
		childData4.put("begin", "开始时间：\n1949-10-1 12:34:56");
		childData4.put("end", "过期时间：\n2013-10-1 12:34:56");
		childData4.put("desc", "描述信息：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		childData4.put("exp", "使用说明：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		child4.add(childData4);
		childData.add(child4);
		
		Map<String, Object> coupon5 = new HashMap<String, Object>();
		coupon5.put("name", "SB券");
		groupData.add(coupon5);
		
		List<Map<String, Object>> child5 = new ArrayList<Map<String,Object>>();
		Map<String, Object> childData5 = new HashMap<String, Object>();
		childData5.put("begin", "开始时间：\n1949-10-1 12:34:56");
		childData5.put("end", "过期时间：\n2013-10-1 12:34:56");
		childData5.put("desc", "描述信息：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		childData5.put("exp", "使用说明：\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		child5.add(childData5);
		childData.add(child5);
		
		String[] groupFrom = {"name"};
		int[] groupTo = {R.id.main_coupon_item_name};
		
		String[] childFrom = {"begin","end","desc","exp"};
		int[] childTo = {R.id.main_coupon_item_child_begin, R.id.main_coupon_item_child_end, R.id.main_coupon_item_child_desc, R.id.main_coupon_item_child_explanation};
		
		return new SimpleExpandableListAdapter(this, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
	}

}
