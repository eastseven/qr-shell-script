package com.quickride.customer.common.activity;

import org.apache.http.cookie.Cookie;

import ac.mm.android.app.ExpandApplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quickride.customer.R;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-29
 * @version 1.0
 */

public class WebViewActivity extends MBaseActivity {
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		String title = getIntent().getStringExtra("title");
		if (null != title && title.trim().length() > 0) {
			setTitle(title);
		}

		setContentView(R.layout.web);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.clearCache(false);

		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// mWebView.setOnKeyListener(new keyListener());
		WebSettings wSetting = mWebView.getSettings();
		// support for javascript
		wSetting.setJavaScriptEnabled(true);
		// wSetting.setPluginState(PluginState.ON);

		mWebView.getSettings().setSaveFormData(true);
		mWebView.getSettings().setSavePassword(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.clearCache(true);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);

				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				setProgressBarIndeterminateVisibility(false);

				super.onPageFinished(view, url);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setMessage(message).setPositiveButton(R.string.confirm, null);
				// 不需要绑定按键事件
				// 屏蔽keycode等于84之类的按键
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
						return true;
					}
				});
				// 禁止响应按back键的事件
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。

				return true;
				// return super.onJsAlert(view, url, message, result);
			}
		});

		mWebView.requestFocus();

		String url = getIntent().getStringExtra("url");
		if (null != url && url.trim().length() > 0) {
			CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();

			Cookie sessionCookie = ((ExpandApplication) getApplication()).getCookieByName("JSESSIONID");

			if (sessionCookie != null) {
				String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain="
						+ sessionCookie.getDomain();

				Log.d("WebViewActivity", "cookie:" + cookieString);

				cookieManager.setCookie(sessionCookie.getDomain(), cookieString);
				CookieSyncManager.getInstance().sync();

				// url += (-1 == url.indexOf("?") ? "?" : "&") + "JSESSIONID=" +
				// sessionCookie.getValue();
			}

			mWebView.loadUrl(url);
		}
	}

	// @Override 返回上一页
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
	// mWebView.goBack();
	//
	// return true;
	// }
	//
	// return super.onKeyDown(keyCode, event);
	// }
}
