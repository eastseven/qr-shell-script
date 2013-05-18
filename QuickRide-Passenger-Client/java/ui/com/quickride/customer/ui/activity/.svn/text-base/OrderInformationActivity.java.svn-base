package com.quickride.customer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.ui.MainTab;

/**
 * 订单信息页面
 * @author eastseven
 *
 */
public class OrderInformationActivity extends Activity {

	private static final String tag = "OrderDetailActivity";
	
	TextView headerTitle;
	Button backButton, nextButton, cancelButton;

	Intent dataIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_information);
		Log.d(tag, getString(R.string.main_title_order_info));
		
		this.initHeaderBar();
		this.initContent();
	}
	
	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_title_order_info));
		
		this.backButton   = (Button) findViewById(R.id.main_header_layout_widget_left);
		this.cancelButton = (Button) findViewById(R.id.main_header_layout_widget_right);

		this.backButton.setText(R.string.main_back);
		this.backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		this.cancelButton.setText(R.string.main_cancel);
		this.cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OrderInformationActivity.this, MainTab.class));
			}
		});
		
	}
	
	void initContent() {
		this.nextButton = (Button) findViewById(R.id.main_next);
		this.nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//检查
				
				//跳转
				dataIntent = new Intent(OrderInformationActivity.this, OrderConfirmActivity.class);
				startActivity(dataIntent);
			}
		});
	}
}
