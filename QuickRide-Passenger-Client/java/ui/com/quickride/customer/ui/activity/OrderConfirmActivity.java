package com.quickride.customer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.ui.MainTab;

/**
 * 订单确认页面
 * 
 * @author eastseven
 *
 */
public class OrderConfirmActivity extends Activity implements OnClickListener {

	private static final String tag = "OrderConfirmActivity";
	
	TextView headerTitle;
	Button backButton, cancelButton, submitButton;

	Intent dataIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_confirm);
		Log.d(tag, getString(R.string.main_title_order_confirm));
		
		this.initHeaderBar();
		this.initContent();
	}
	
	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_title_order_confirm));
		
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
				startActivity(new Intent(OrderConfirmActivity.this, MainTab.class));
			}
		});
	}
	
	void initContent() {
		this.submitButton = (Button) findViewById(R.id.main_order_confirm);
		this.submitButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		//检查
		//提交订单
		dataIntent = new Intent(this, OrderPayTypeActivity.class);
		startActivity(dataIntent);
	}
}
