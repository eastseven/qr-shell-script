package com.quickride.customer.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.ui.MainTab;

/**
 * 订单服务详情
 * @author eastseven
 *
 */
public class OrderServiceDetailsActivity extends Activity {

	private static final String tag = "OrderServiceDetailsActivity";
	
	//header bar
	TextView headerTitle;
	Button backButton, nextButton, cancelButton;
	
	//center content
	EditText productName, carGrade, price, pickupTime, pickupAddress, dischargeAddress, flightNo;
	
	//data
	Bundle orderProduct;
	Intent dataIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_service_details);
		Log.d(tag, "订单服务详情");
		
		this.orderProduct = getIntent().getBundleExtra("orderProduct");
		
		this.initHeaderBar();
		this.initContent();
	}
	
	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(R.string.main_service_details);
		
		this.backButton = (Button) findViewById(R.id.main_header_layout_widget_left);
		this.backButton.setText(R.string.main_back);
		this.backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		this.cancelButton = (Button) findViewById(R.id.main_header_layout_widget_right);
		this.cancelButton.setText(R.string.main_cancel);
		this.cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OrderServiceDetailsActivity.this, MainTab.class));
			}
		});
	}
	
	void initContent() {
		this.productName      = (EditText) findViewById(R.id.main_sd_product_name);
		this.carGrade         = (EditText) findViewById(R.id.main_sd_car_grade);
		this.price            = (EditText) findViewById(R.id.main_sd_price);
		this.pickupTime       = (EditText) findViewById(R.id.main_sd_pickup_time);
		this.pickupAddress    = (EditText) findViewById(R.id.main_sd_pickup_address);
		this.dischargeAddress = (EditText) findViewById(R.id.main_sd_discharge_address);
		this.flightNo         = (EditText) findViewById(R.id.main_sd_flight_number);
		this.nextButton       = (Button)   findViewById(R.id.main_next);
		
		this.nextButton.setText(R.string.main_next);
		this.nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//检查
				
				//跳转
				dataIntent = new Intent(OrderServiceDetailsActivity.this, OrderInformationActivity.class);
				dataIntent.putExtra("orderProduct", orderProduct);
				startActivity(dataIntent);
			}
		});
		
		String text = "";
		Object value = null;
		for (String key : orderProduct.keySet()) {
			value = orderProduct.get(key);
			
			if("productName".equals(key)) {
				text = getString(R.string.main_sd_product_name) + ": " + value;
				this.productName.setText(text);
				
			} else if ("carGrade".equals(key)) {
				text = getString(R.string.main_sd_car_grade) + ": " + value;
				this.carGrade.setText(text);
				
			} else if ("price".equals(key)) {
				text = getString(R.string.main_sd_price) + ": " + value;
				this.price.setText(text);
				
			} else if ("pickupTime".equals(key)) {
				text = getString(R.string.main_sd_pickup_time) + ": " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date((Long)value));
				this.pickupTime.setText(text);
				
			} else if ("pickupAddress".equals(key)) {
				text = "" + value;
				this.pickupAddress.setText(text);
				
			} else if ("dischargeAddress".equals(key)) {
				text = "" + value;
				this.dischargeAddress.setText(text);
			} 
			
		}
		
	}
	
}
