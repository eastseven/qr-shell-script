package com.quickride.customer.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.quickride.customer.R;

public class CouponListAdapter extends BaseExpandableListAdapter {

	public static String tag = "QR_CouponListAdapter";
	
	Context context;
	List<Map<String, Object>> result;
	List<Map<String, Object>> groupData;
	List<Map<String, Object>> childData;
	final static String[] groupFrom = {};
	final static int[] groupTo = {};
	final static String[] childFrom = {};
	final static int[] childTo = {};

	/*
	 * useDesc=可以直接使用, 
	 * couponNo=00000A0300032016, 
	 * takeDesc=可以直接获取, 
	 * couponStatus=3, 
	 * couponType=体验券, 
	 * description=自动发放；使用该券，车费类似出租车, 
	 * name=爱订车初冬体验券, 
	 * endDate=2012-11-30 18:00:00, 
	 * logoUrl=http://192.168.1.101:8079/2012/11/6/11yuequan.jpg, 
	 * beginDate=2012-11-06 13:40:00, 
	 * remainCount=656
	 * */
	public CouponListAdapter(Context context, List<Map<String, Object>> result) {
		this.context = context;
		this.result  = result;
		this.groupData = new ArrayList<Map<String,Object>>();
		this.childData = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = null;
		for (Map<String, Object> coupon : this.result) {
			map = new HashMap<String, Object>();
			map.put("name", coupon.get("name"));
			this.groupData.add(map);
			
			map.clear();
			
			map.put("", "");
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.main_coupon_item, null);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
