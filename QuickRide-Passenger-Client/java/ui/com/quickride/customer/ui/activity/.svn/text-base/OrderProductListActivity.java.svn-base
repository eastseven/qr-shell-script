package com.quickride.customer.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.util.DebugUtil;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.ui.adapter.OrderProductAdapter;

/**
 * 特价产品列表
 * @author eastseven
 *
 */
public class OrderProductListActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String tag = "OrderProductListActivity";
	
	ListView orderProductListView;
	ListAdapter adapter;
	TextView title;
	Button backButton, rightButton;
	Button earlierButton, laterButton;
	
	Intent dataIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_product_list);
		Log.d(tag, "特价产品列表");
		
		this.orderProductListView = (ListView) findViewById(R.id.main_order_product_list);
		this.orderProductListView.setOnItemClickListener(this);
		
		this.initHeaderBar();
		this.loadData();
		
	}

	void initHeaderBar() {
		
		this.title = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.title.setText(R.string.main_title_coupon_result);
		
		this.rightButton = (Button) findViewById(R.id.main_header_layout_widget_right);
		this.rightButton.setVisibility(View.GONE);

		this.backButton = (Button) findViewById(R.id.main_header_layout_widget_left);
		this.backButton.setText(R.string.main_back);
		this.backButton.setOnClickListener(this);
		
		this.earlierButton = (Button) findViewById(R.id.main_footer_layout_widget_earlier);
		this.laterButton   = (Button) findViewById(R.id.main_footer_layout_widget_later);
		this.earlierButton.setOnClickListener(this);
		this.laterButton.setOnClickListener(this);
		
	}

	void loadData() {
		final ProgressDialog progressDialog = ProgressDialog.show(OrderProductListActivity.this, null, getString(R.string.waitting), true, true);
		EndpointClient request = new EndpointClient(OrderProductListActivity.this) {
			
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				HashMap<String, String> map = new HashMap<String, String>();
				for(String key : getIntent().getExtras().keySet()) {
					String value = getIntent().getExtras().get(key).toString();
					map.put(key, value);
				}
				
				return getOrderProducts(map);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();
				DebugUtil.print(result, getClass());
				
				if(result.containsKey("statusCode") && !result.get("statusCode").equals("0000")) {
					Toast.makeText(OrderProductListActivity.this, result.get("statusMessage").toString(), Toast.LENGTH_LONG).show();
					return;
				}
				
				Object object = result.get("products");
				if(object instanceof ArrayList) {
					ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) object;
					ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
					for (Object rowData : list) {
						HashMap<String, Object> orderProduct = (HashMap<String, Object>) rowData;
						data.add(orderProduct);
					}
					adapter = new OrderProductAdapter(OrderProductListActivity.this, R.layout.main_order_product_item, data);
					orderProductListView.setAdapter(adapter);
				}
				
			}
		};
		request.execute();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_header_layout_widget_left:
			this.onBackPressed();
			break;
		case R.id.main_footer_layout_widget_earlier:
			Toast.makeText(v.getContext(), getString(R.string.main_earlier), Toast.LENGTH_LONG).show();
			break;
		case R.id.main_footer_layout_widget_later:
			Toast.makeText(v.getContext(), getString(R.string.main_later), Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HashMap<String, Object> orderProduct = (HashMap<String, Object>) this.orderProductListView.getAdapter().getItem(position);
		
		Log.d(tag+".onItemClick", "parent="+parent+", view="+view+", position="+position+", id="+id);
		Log.d(tag, "getAdapter.getItem=" + orderProduct);
		
		this.dataIntent = new Intent(OrderProductListActivity.this, OrderServiceDetailsActivity.class);
		Bundle bundle = new Bundle();
		for (String key : orderProduct.keySet()) {
			Object value = orderProduct.get(key);
			if(value instanceof String) {
				bundle.putString(key, value.toString());
			} else if (value instanceof Integer) {
				bundle.putInt(key, (Integer)value);
			} else if (value instanceof Long) {
				bundle.putLong(key, (Long)value);
			} else if (value instanceof Double) {
				bundle.putDouble(key, (Double)value);
			}
		}
		this.dataIntent.putExtra("orderProduct", bundle);
		
		startActivity(dataIntent);
	}
	
}
