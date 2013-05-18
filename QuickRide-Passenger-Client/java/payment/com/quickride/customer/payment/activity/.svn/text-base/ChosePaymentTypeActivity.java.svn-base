package com.quickride.customer.payment.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinapnr.payment2.Payment;
import com.chinapnr.payment2.PluginSetting;
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

public class ChosePaymentTypeActivity extends MBaseActivity {
	private ProgressDialog progressDialog;

	private TextView rentCostTextView;

	private RadioGroup payTypeRadioGroup;
	private RadioGroup couponsRadioGroup;

	private CheckBox useCouponsCheckBox;
	private CheckBox invoiceCheckBox;

	private Map<String, Object> order;

	private Integer cardPrice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chose_payment_type);

		order = (Map<String, Object>) getIntent().getSerializableExtra("order");
		if (null == order) {
			viewOrderDetail(getIntent().getStringExtra("orderNo"));
		} else {
			setupOrderDetail(order);
		}
	}

	private void viewOrderDetail(final String orderNo) {
		progressDialog = ProgressDialog.show(this, null, getString(R.string.loading), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.alert)
						.setTitle(R.string.loading).setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								ChosePaymentTypeActivity.this.viewOrderDetail(orderNo);
							}
						}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

				return;
			}
		});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> orderId = new HashMap<String, String>();
				orderId.put("orderNo", orderNo);

				return viewOrderDetail(orderId);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									progressDialog.dismiss();

									ChosePaymentTypeActivity.this.viewOrderDetail(orderNo);
								}
							}).setCancelable(true).setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									progressDialog.dismiss();

									ChosePaymentTypeActivity.this.viewOrderDetail(orderNo);
								}
							}).show();

					return;
				}

				order = result;

				setupOrderDetail(result);

				progressDialog.dismiss();
			}
		}.execute();
	}

	private void setupOrderDetail(final Map<String, Object> order) {
		cardPrice = (int) Integer.valueOf(order.get("cardPrice").toString());

		String cardSalesMessage = (String) order.get("cardSalesMessage");
		if (null != cardSalesMessage && cardSalesMessage.trim().length() > 0) {
			TextView cardSalesMessageTextView = (TextView) findViewById(R.id.content);
			cardSalesMessageTextView.setText(cardSalesMessage);
			cardSalesMessageTextView.setVisibility(View.VISIBLE);
		}

		String favoreMessage = (String) order.get("favoreMessage");
		if (null != favoreMessage && favoreMessage.trim().length() > 0) {
			TextView favoreMessageTextView = (TextView) findViewById(R.id.description);
			favoreMessageTextView.setText(favoreMessage);
			favoreMessageTextView.setVisibility(View.VISIBLE);
		}

		((TextView) findViewById(R.id.order_id)).setText(getString(R.string.order_id) + "：" + order.get("orderNo"));

		rentCostTextView = (TextView) findViewById(R.id.rent_cost);
		setRentCostTextView(cardPrice.toString());

		payTypeRadioGroup = (RadioGroup) findViewById(R.id.pay_type);
		useCouponsCheckBox = (CheckBox) findViewById(R.id.use_coupons);
		invoiceCheckBox = (CheckBox) findViewById(R.id.invoice);

		final Button confirmButton = (Button) findViewById(R.id.pay);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String payType = ((RadioButton) payTypeRadioGroup.findViewById(payTypeRadioGroup
						.getCheckedRadioButtonId())).getText().toString();
				String rentCost = rentCostTextView.getText().toString();

				String message = getString(R.string.pay_type) + "：" + payType;

				if (null != useCouponsCheckBox.getTag()) {
					Map<String, ?> coupons = (Map<String, ?>) ((View) useCouponsCheckBox.getTag()).getTag();
					String couponsName = (String) coupons.get("name");

					message += "\n" + getString(R.string.use_coupons) + "：《" + couponsName + "》";
				} else {
					message += "\n（ 没有使用优惠券）";
				}

				message += "\n\n" + rentCost;

				new AlertDialog.Builder(ChosePaymentTypeActivity.this).setMessage(message)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								selectPayType(order, getPayType());
							}
						}).setNegativeButton(R.string.cancel, null).show();
			}
		});

		Button unsubscribeButton = (Button) findViewById(R.id.unsubscribe);
		unsubscribeButton.setTag(order);

		final RadioButton creditCardButton = (RadioButton) findViewById(R.id.credit_card);
		final RadioButton cashButton = (RadioButton) findViewById(R.id.cash);

		useCouponsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					getEnableCoupons(order, getPayType());
				} else {
					couponsRadioGroup.setVisibility(View.INVISIBLE);

					useCouponsCheckBox.setTag(null);
					useCouponsCheckBox.setText(getString(R.string.use_coupons));

					int payType = getPayType();
					if (0 == payType) {
						setRentCostTextView(order.get("price").toString());
					} else if (1 == payType) {
						setRentCostTextView(cardPrice.toString());
					}
				}
			}
		});

		payTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (creditCardButton.getId() == checkedId) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.credit_card_pay).setMessage("请注意：您需要立即在线支付，如超时未付款，订单会被系统自动取消。")
							.setPositiveButton(R.string.confirm, null).show();

					setRentCostTextView(cardPrice.toString());

					confirmButton.setText(R.string.pay);
				} else if (cashButton.getId() == checkedId) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.cash_pay).setMessage("您需要在乘车时支付现金。")
							.setPositiveButton(R.string.confirm, null).show();

					setRentCostTextView(order.get("price").toString());

					confirmButton.setText(R.string.submit);
				}

				if (useCouponsCheckBox.isChecked()) {
					useCouponsCheckBox.setChecked(false);
				}
			}
		});

		couponsRadioGroup = (RadioGroup) findViewById(R.id.coupons_list);
	}

	private void setRentCostTextView(String price) {
		rentCostTextView.setText(getString(R.string.rent_cost) + "：" + price + getString(R.string.yuan));
	}

	private int getPayType() {
		if (payTypeRadioGroup.indexOfChild(payTypeRadioGroup.findViewById(payTypeRadioGroup.getCheckedRadioButtonId())) == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	private void getEnableCoupons(final Map<String, Object> order, final Integer payType) {
		final ProgressDialog progressDialog = ProgressDialog.show(ChosePaymentTypeActivity.this, null,
				getString(R.string.waitting), true, true);

		useCouponsCheckBox.setTag(null);
		couponsRadioGroup.removeAllViews();

		new EndpointClient(ChosePaymentTypeActivity.this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("orderNo", (String) order.get("orderNo"));
				info.put("payType", payType.toString());

				return getEnableCoupons(info);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									ChosePaymentTypeActivity.this.getEnableCoupons(order, payType);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									useCouponsCheckBox.setChecked(false);

									Toast.makeText(ChosePaymentTypeActivity.this, R.string.request_fail,
											Toast.LENGTH_LONG).show();
								}
							}).setCancelable(true).show();

					return;
				}

				final List<Map<String, ?>> couponsList = (List<Map<String, ?>>) result.get("result");

				if (null != couponsList && !couponsList.isEmpty()) {
					RadioGroup chooseCouponsRadioGroup = new RadioGroup(ChosePaymentTypeActivity.this);

					for (Map<String, ?> coupons : couponsList) {
						String name = "《" + (String) coupons.get("name") + "》";

						int couponCount = (Integer) coupons.get("couponCount");
						if (couponCount > 1) {
							name += " (" + couponCount + "张)";
						}

						int fee = (Integer) coupons.get("fee");
						name += "\n优惠后实付金额：" + fee + getString(R.string.yuan);

						name += "\n开始时间：" + coupons.get("beginDate");
						name += "\n过期时间：" + coupons.get("endDate");

						RadioButton couponsRadioButton = new RadioButton(ChosePaymentTypeActivity.this);
						couponsRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
						couponsRadioButton.setTextColor(getResources().getColor(R.color.dark));
						couponsRadioButton.setBackgroundColor(getResources().getColor(R.color.white));
						couponsRadioButton.setTag(coupons);

						if (!(Boolean) coupons.get("status")) {
							couponsRadioButton.setEnabled(false);
							couponsRadioButton.setBackgroundColor(getResources().getColor(R.color.darkgrey));
							couponsRadioButton.setButtonDrawable(android.R.color.transparent);

							name += "\n不满足以下使用条件：" + coupons.get("notEnoughFilter");
						}

						couponsRadioButton.setText(name);

						chooseCouponsRadioGroup.addView(couponsRadioButton);

						// View divider = new
						// View(ChosePaymentTypeActivity.this);
						// divider.setLayoutParams(new
						// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						// 1));
						// divider.setBackgroundColor(getResources().getColor(R.color.lightblue));
						// chooseCouponsRadioGroup.addView(divider);
					}

					chooseCouponsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							couponsRadioGroup.removeAllViews();

							setRentCostTextView(couponsList.get(group.indexOfChild(group.findViewById(checkedId)))
									.get("fee").toString());

							useCouponsCheckBox.setTag(group.findViewById(checkedId));

							RadioButton couponsRadioButton = new RadioButton(ChosePaymentTypeActivity.this);
							couponsRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
							couponsRadioButton.setTextColor(getResources().getColor(R.color.dark));
							couponsRadioButton.setBackgroundColor(getResources().getColor(R.color.white));
							couponsRadioButton.setChecked(true);
							couponsRadioButton.setClickable(false);

							couponsRadioButton.setText("《"
									+ (String) couponsList.get(group.indexOfChild(group.findViewById(checkedId))).get(
											"name") + "》");

							couponsRadioGroup.addView(couponsRadioButton);
						}
					});

					couponsRadioGroup.setVisibility(View.VISIBLE);

					ScrollView scrollView = new ScrollView(ChosePaymentTypeActivity.this);
					scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT,
							ScrollView.LayoutParams.FILL_PARENT));
					scrollView.addView(chooseCouponsRadioGroup);

					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setTitle("选择优惠券")
							.setIcon(R.drawable.ic_menu_more).setView(scrollView)
							.setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									useCouponsCheckBox.setChecked(false);
								}
							}).setPositiveButton(R.string.confirm, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (null == useCouponsCheckBox.getTag()) {
										useCouponsCheckBox.setChecked(false);

										Toast.makeText(ChosePaymentTypeActivity.this, "没有使用优惠券", Toast.LENGTH_LONG)
												.show();
									}
								}
							}).setNegativeButton(R.string.cancel, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									useCouponsCheckBox.setChecked(false);
								}
							}).show();

				} else {
					useCouponsCheckBox.setChecked(false);

					useCouponsCheckBox.setTag(null);
					useCouponsCheckBox.setText(getString(R.string.use_coupons) + " (没有可用的优惠券)");

					Toast.makeText(ChosePaymentTypeActivity.this, "没有可用的优惠券", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	private void selectPayType(final Map<String, ?> order, final int payType) {
		final ProgressDialog progressDialog = ProgressDialog.show(ChosePaymentTypeActivity.this, null,
				getString(R.string.waitting), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.alert)
						.setTitle(R.string.loading).setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								ChosePaymentTypeActivity.this.selectPayType(order, payType);
							}
						}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

				return;
			}
		});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> selectPayType = new HashMap<String, String>();
				selectPayType.put("orderNo", (String) order.get("orderNo"));
				selectPayType.put("payType", String.valueOf(payType));
				selectPayType.put("hasInvoice", String.valueOf(invoiceCheckBox.isChecked()));

				if (null != useCouponsCheckBox.getTag()) {
					Map<String, ?> coupons = (Map<String, ?>) ((View) useCouponsCheckBox.getTag()).getTag();
					String couponId = (String) coupons.get("couponId");

					selectPayType.put("couponId", couponId);
				}

				return selectPayType(selectPayType);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				if (null == result) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									ChosePaymentTypeActivity.this.selectPayType(order, payType);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Toast.makeText(ChosePaymentTypeActivity.this, R.string.request_fail,
											Toast.LENGTH_LONG).show();
								}
							}).setCancelable(true).show();

					progressDialog.dismiss();

					return;
				}

				if ("1810".equals((String) result.get("statusCode"))) {
					progressDialog.dismiss();

					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.request_fail).setMessage((String) result.get("statusMessage"))
							.setPositiveButton(R.string.confirm, null).setCancelable(false).show();

					return;
				}

				if ("1902".equals((String) result.get("statusCode"))
						|| "1905".equals((String) result.get("statusCode"))) {
					progressDialog.dismiss();

					useCouponsCheckBox.setChecked(false);

					Toast.makeText(ChosePaymentTypeActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				if ("1801".equals((String) result.get("statusCode"))
						|| "1805".equals((String) result.get("statusCode"))
						|| "1806".equals((String) result.get("statusCode"))) {
					progressDialog.dismiss();

					Toast.makeText(ChosePaymentTypeActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					startActivity(new Intent(ChosePaymentTypeActivity.this, DistributorActivity.class));

					finish();

					return;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					progressDialog.dismiss();

					Toast.makeText(ChosePaymentTypeActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				if (null != (String) result.get("payUrl")) {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this)
							.setMessage((String) result.get("payTimeAlert"))
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									PluginSetting pluginSetting = new PluginSetting((String) result.get("payUrl"),
											getString(R.string.service_phone));

									Intent intent = new Intent();
									// 设置跳转插件Activity
									intent.setClass(ChosePaymentTypeActivity.this, Payment.class);
									// 把插件设置类的对象也放到Intent 里 Key 值为chinapnr
									intent.putExtra("chinapnr", pluginSetting);

									startActivityForResult(intent, 100);
								}
							}).setCancelable(false).show();
				} else {
					new AlertDialog.Builder(ChosePaymentTypeActivity.this).setMessage("系统已接受您的请求，我们将会及时为您派车")
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									startActivity(new Intent(ChosePaymentTypeActivity.this, DistributorActivity.class));

									finish();
								}
							}).setCancelable(false).show();
				}

				progressDialog.dismiss();
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("Pay", "requestCode=" + requestCode + ", resultCode=" + resultCode);

		if (requestCode == 100) {
			switch (resultCode) {
			case 10:
				new AlertDialog.Builder(ChosePaymentTypeActivity.this).setMessage("支付成功！\n系统已接受您的请求，我们将会及时为您派车")
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								startActivity(new Intent(ChosePaymentTypeActivity.this, DistributorActivity.class));

								finish();
							}
						}).setCancelable(false).show();

				break;
			case 0:
			case 11:
				new AlertDialog.Builder(ChosePaymentTypeActivity.this).setMessage("支付失败！\n\n请重新支付")
						.setPositiveButton(R.string.confirm, null).setCancelable(false).show();

				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(ChosePaymentTypeActivity.this).setIcon(R.drawable.alert).setTitle("没有完成支付")
				.setMessage("您还没有完成支付！请继续支付或退订 ").setPositiveButton(R.string.confirm, null).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}
}
