package com.quickride.customer.ui.activity;

import com.quickride.customer.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * 常见问题
 * 
 * @author eastseven
 * 
 */
public class FaqActivity extends Activity {

	private static final String tag = "QR_FaqActivity";
	
	TextView headerTitle;
	Button backButton, nextButton;
	
	WebView faqWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_faq);
		Log.d(tag, getString(R.string.main_more_faq));
		
		this.initHeaderBar();
		this.initContent();
	}
	
	void initHeaderBar() {

		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_more_faq));
		
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
	
	void initContent() {
		this.faqWebView = (WebView) findViewById(R.id.main_faq_webview);
		this.faqWebView.loadUrl("http://www.idingche.com.cn/faq.jsp");
	}
}
