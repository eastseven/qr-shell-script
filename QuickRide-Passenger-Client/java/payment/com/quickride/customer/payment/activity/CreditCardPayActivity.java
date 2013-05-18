package com.quickride.customer.payment.activity;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.util.graphics.DisplayUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.DistributorActivity;
import com.quickride.customer.common.activity.MBaseActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-4-9
 * @version 1.0
 */

public class CreditCardPayActivity extends MBaseActivity {
	private EditText cardNoTextView;
	private EditText userPasswordEditText;
	private EditText captchaEditText;
	private TextView verificationCodeTextView;
	private TextView arriveTimeTextView;

	private DisplayUtil displayUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.credit_card_pay);

		final Map<String, Object> order = (Map<String, Object>) getIntent().getSerializableExtra("order");

		displayUtil = new DisplayUtil(this);

		cardNoTextView = (EditText) findViewById(R.id.name);
		userPasswordEditText = (EditText) findViewById(R.id.user_password);
		captchaEditText = (EditText) findViewById(R.id.verification_code);
		arriveTimeTextView = (EditText) findViewById(R.id.arrive_time);

		((TextView) findViewById(R.id.rent_cost)).setText(getString(R.string.pay) + "：" + order.get("price")
				+ getString(R.string.yuan));

		verificationCodeTextView = (TextView) findViewById(R.id.get_verification_code);
		verificationCodeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setVerificationCode();
			}
		});

		Button payButton = (Button) findViewById(R.id.pay);
		payButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!validatePayInfo()) {
					return;
				}

				final ProgressDialog progressDialog = ProgressDialog.show(CreditCardPayActivity.this, null,
						getString(R.string.waitting), true, true);

				pay(progressDialog, order);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		setVerificationCode();
	}

	private void setVerificationCode() {
		verificationCodeTextView.setEnabled(false);
		verificationCodeTextView.setTextColor(getResources().getColor(R.color.ivory));

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getCaptcha();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				verificationCodeTextView.setEnabled(true);
				verificationCodeTextView.setTextColor(getResources().getColor(R.color.dark));

				if (null != result.get("captcha")) {
					Drawable verificationCodeImage = displayUtil.bytes2Drawable((byte[]) result.get("captcha"));
					verificationCodeImage.setBounds(0, 0, displayUtil.dip2px(106), displayUtil.dip2px(42));
					verificationCodeTextView.setCompoundDrawables(null, verificationCodeImage, null, null);

					captchaEditText.setText("");
				}
			}
		}.execute();
	}

	private boolean validatePayInfo() {
		if (null == cardNoTextView.getText().toString() || 16 != cardNoTextView.getText().toString().trim().length()) {
			Toast.makeText(this, "信用卡卡号应该是16位", Toast.LENGTH_SHORT).show();

			cardNoTextView.setError("信用卡卡号应该是16位数字");

			return false;
		}

		if (null == userPasswordEditText.getText().toString()
				|| userPasswordEditText.getText().toString().trim().length() != 3) {
			Toast.makeText(this, "应该是三位数字", Toast.LENGTH_SHORT).show();

			userPasswordEditText.setError("应该是三位数字");

			return false;
		}

		if (null == arriveTimeTextView.getText().toString()
				|| arriveTimeTextView.getText().toString().trim().length() != 2) {
			Toast.makeText(this, "应该是两位数字", Toast.LENGTH_SHORT).show();

			arriveTimeTextView.setError("应该是两位数字");

			return false;
		}

		if (null == captchaEditText.getText().toString() || "".equals(captchaEditText.getText().toString().trim())) {
			Toast.makeText(this, R.string.verification_code_hint, Toast.LENGTH_SHORT).show();

			captchaEditText.setError(getString(R.string.verification_code_hint));

			return false;
		}

		return true;
	}

	private void pay(final ProgressDialog progressDialog, final Map<String, ?> order) {
		new EndpointClient(CreditCardPayActivity.this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> orderId = new HashMap<String, String>();
				orderId.put("orderNo", (String) order.get("orderNo"));

				// return pay(orderId);
				return (Map<String, Object>) null;
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(CreditCardPayActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									CreditCardPayActivity.this.pay(progressDialog, order);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									progressDialog.dismiss();

									Toast.makeText(CreditCardPayActivity.this, R.string.request_fail, Toast.LENGTH_LONG)
											.show();
								}
							}).setCancelable(false).show();

					return;
				}

				Toast.makeText(CreditCardPayActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();

				Intent intent = new Intent(CreditCardPayActivity.this, DistributorActivity.class);
				startActivity(intent);

				progressDialog.dismiss();
			}
		}.execute();
	}
}
