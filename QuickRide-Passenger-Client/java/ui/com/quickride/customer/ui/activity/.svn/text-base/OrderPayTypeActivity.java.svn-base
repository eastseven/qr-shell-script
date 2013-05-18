package com.quickride.customer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.ui.MainTab;

/**
 * 支付方式页面
 * 
 * @author eastseven
 * 
 */
public class OrderPayTypeActivity extends Activity implements OnClickListener {

	private static final String tag = "OrderPayTypeActivity";
	
	TextView headerTitle;
	Button payButton, homeButton, cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_pay_type);
		Log.d(tag, getString(R.string.main_title_pay_type));
		
		this.initHeaderBar();
		this.initContent();
	}
	
	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(R.string.main_title_pay_type);
		
		this.cancelButton = (Button) findViewById(R.id.main_header_layout_widget_left);
		this.cancelButton.setText(R.string.main_order_cancel);
		this.cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//订单退订
				Toast.makeText(v.getContext(), "订单退订\n未完待续。。。", Toast.LENGTH_LONG).show();
			}
		});
		
		this.homeButton = (Button) findViewById(R.id.main_header_layout_widget_right);
		this.homeButton.setText(R.string.main_home);
		this.homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OrderPayTypeActivity.this, MainTab.class));
			}
		});
	}
	
	void initContent() {
		this.payButton = (Button) findViewById(R.id.main_order_pay);
		this.payButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "未完待续。。。", Toast.LENGTH_LONG).show();
	}
}
