package com.quickride.customer.ui.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.DistributorActivity;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.util.DateUtil;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.payment.activity.ChosePaymentTypeActivity;
import com.quickride.customer.report.activity.MyOrderListActivity;

public class MyOrderDetailActivity extends MGestureSwitchPageActivity {

	public static String Tag = "QR_MyOrderDetailActivity";

	TextView headerTitle;
	Button backButton, nextButton;
	
	private DateUtil dateUtil;
	private ProgressDialog progressDialog;
	private AlertDialog reviewDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_order_detail);

		this.initHeaderBar();
		
		dateUtil = new DateUtil(this);

		@SuppressWarnings("unchecked")
		final Map<String, Object> order = (Map<String, Object>) getIntent().getSerializableExtra("order");
		if (null == order) {
			viewOrderDetail(getIntent().getStringExtra("orderNo"));
		} else {
			setupOrderDetail(order);
		}
	}

	void initHeaderBar() {
		this.headerTitle = (TextView) findViewById(R.id.main_header_layout_widget_title);
		this.headerTitle.setText(getString(R.string.main_more_my_order));
		
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
	
	private void setupOrderDetail(final Map<String, Object> order) {
		((TextView) findViewById(R.id.order_id)).setText(getString(R.string.order_id) + "：" + order.get("orderNo"));

		Integer orderStatus = (Integer) order.get("orderStatus");
		((TextView) findViewById(R.id.rent_car_status)) .setText(getString(R.string.rent_car_status) + "：" + getResources().getStringArray(R.array.order_status)[orderStatus - 2]);

		((TextView) findViewById(R.id.order_car_time)).setText(getString(R.string.order_car_time) + "：" + dateUtil.getDateString(Long.valueOf((String) order.get("orderTime"))));

		Integer isPay = (Integer) order.get("isPay");

		String isPayString = "";
		if (isPay == 0) {
			isPayString = getString(R.string.arrearage);
		} else if (isPay == 1) {
			isPayString = getString(R.string.paied);
		} else if (isPay == 2) {
			isPayString = getString(R.string.refund);
		}

		((TextView) findViewById(R.id.rent_cost))
				.setText(getString(R.string.rent_cost) + "："
						+ order.get("price") + getString(R.string.yuan) + " （"
						+ isPayString + "）");

		Integer payType = (Integer) order.get("payType");

		if (null != payType) {
			TextView payTypeTextView = ((TextView) findViewById(R.id.pay_type));
			payTypeTextView.setText(getString(R.string.pay_type) + "："
					+ getResources().getStringArray(R.array.pay_type)[payType]);
			payTypeTextView.setVisibility(View.VISIBLE);
		}

		Integer points = (Integer) order.get("points");
		if (null != points) {
			TextView pointsTextView = ((TextView) findViewById(R.id.points));
			pointsTextView.setText("获得积分" + "：" + points);
			pointsTextView.setVisibility(View.VISIBLE);
		}

		Integer paymentAmount = (Integer) order.get("paymentAmount");
		if (null != paymentAmount) {
			TextView paymentAmountView = ((TextView) findViewById(R.id.payment_amount));
			paymentAmountView.setText("应付金额" + "：" + paymentAmount
					+ getString(R.string.yuan));
			paymentAmountView.setVisibility(View.VISIBLE);
		}

		String couponsName = (String) order.get("couponsName");
		if (null != couponsName && couponsName.trim().length() > 0) {
			TextView couponsTextView = ((TextView) findViewById(R.id.use_coupons));
			couponsTextView.setText("使用优惠券" + "：" + couponsName);
			couponsTextView.setVisibility(View.VISIBLE);
		}

		((TextView) findViewById(R.id.rent_mileage))
				.setText(getString(R.string.rent_mileage) + "："
						+ order.get("distance") + getString(R.string.km));

		((TextView) findViewById(R.id.get_on_car_time))
				.setText(getString(R.string.get_on_car_time)
						+ "："
						+ dateUtil.getDateString(Long.valueOf((String) order
								.get("pickupTime"))));

		((TextView) findViewById(R.id.get_on_car_location))
				.setText(getString(R.string.start_place) + "："
						+ order.get("pickupAddress"));

		((TextView) findViewById(R.id.get_off_car_location))
				.setText(getString(R.string.end_place) + "："
						+ order.get("unloadAddress"));

		((TextView) findViewById(R.id.car_type))
				.setText(getString(R.string.car_type) + "："
						+ order.get("carModel"));

		if (null != order.get("licenseNo")) {
			((TextView) findViewById(R.id.car_license))
					.setText(getString(R.string.car_license) + "："
							+ order.get("licenseNo"));
		}

		if (null != order.get("driverAverageRate")) {
			((RatingBar) findViewById(R.id.driver_grade)).setRating(Float
					.valueOf((String) order.get("driverAverageRate")));

			findViewById(R.id.grade).setVisibility(View.VISIBLE);
		}

		if (null != order.get("driverNo")) {
			((TextView) findViewById(R.id.driver_id))
					.setText(getString(R.string.driver_id) + "："
							+ order.get("driverNo"));
		}

		if (null != order.get("driverName")) {
			((TextView) findViewById(R.id.driver_name))
					.setText(getString(R.string.driver_name) + "："
							+ order.get("driverName"));
		}

		if (orderStatus == StatusCode.FINISHED_ORDER) {
			TextView unloadTimeTextView = (TextView) findViewById(R.id.get_off_car_time);
			unloadTimeTextView.setText(getString(R.string.arrived_time)
					+ "："
					+ dateUtil.getDateString(Long.valueOf((String) order
							.get("unloadTime"))));
			unloadTimeTextView.setVisibility(View.VISIBLE);

			showReviewInfo(order);
		} else if (orderStatus == StatusCode.BOOKED_CAR
				|| orderStatus == StatusCode.BOOK_CAR) {
			findViewById(R.id.rootLayout).setVisibility(View.VISIBLE);

			Button unsubscribeButton = (Button) findViewById(R.id.unsubscribe);
			unsubscribeButton.setTag(order);

			if (null == payType || (1 == payType && 0 == isPay)) {
				Button payButton = (Button) findViewById(R.id.pay);
				payButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MyOrderDetailActivity.this,
								ChosePaymentTypeActivity.class);
						intent.putExtra("order", (Serializable) order);

						startActivity(intent);

						finish();
					}
				});

				payButton.setVisibility(View.VISIBLE);
			}
		}
	}

	private void showReviewInfo(final Map<String, Object> order) {
		Integer grade = (Integer) order.get("rate");
		if (null == grade || grade == 0) {
			Button addCommentButton = (Button) findViewById(R.id.add_review_button);
			addCommentButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					final View textEntryView = LayoutInflater.from(
							MyOrderDetailActivity.this).inflate(
							R.layout.review_dialog, null);

					reviewDialog = new AlertDialog.Builder(
							MyOrderDetailActivity.this)
							.setIcon(R.drawable.ic_menu_more).setTitle("添加评价")
							.setView(textEntryView).create();

					Button addCommentCommit = (Button) textEntryView
							.findViewById(R.id.confirm);
					addCommentCommit
							.setOnClickListener(new Button.OnClickListener() {
								@Override
								public void onClick(View v) {
									EditText addCommentEdit = (EditText) textEntryView
											.findViewById(R.id.edit_review);

									final String commentValue = addCommentEdit
											.getEditableText().toString();
									final ProgressDialog progressDialog = ProgressDialog
											.show(MyOrderDetailActivity.this,
													null,
													getString(R.string.loading),
													true, true);

									RatingBar ratingBar = (RatingBar) textEntryView
											.findViewById(R.id.edit_stars);

									final int grade = (int) ratingBar
											.getRating();

									new EndpointClient(
											MyOrderDetailActivity.this) {
										@Override
										protected Map<String, Object> doInBackground(
												Void... arg0) {
											Map<String, String> rate = new HashMap<String, String>();
											rate.put("orderNo", (String) order
													.get("orderNo"));
											rate.put("rate",
													String.valueOf(grade));
											rate.put("comment", commentValue);

											return rateDriver(rate);
										}

										@Override
										protected void onEndpointClientPostExecute(
												Map<String, Object> result) {
											if (null == result) {
												progressDialog.dismiss();

												Toast.makeText(
														MyOrderDetailActivity.this,
														R.string.request_fail,
														Toast.LENGTH_LONG)
														.show();

												return;
											}

											if (StatusCode.SUCCESS
													.equals((String) result
															.get("statusCode"))) {
												Toast.makeText(
														MyOrderDetailActivity.this,
														"评价成功",
														Toast.LENGTH_LONG)
														.show();

												order.put("rate", grade);
												order.put("comment",
														commentValue);
												// orderDao.update(order);

												Intent intent = new Intent(
														MyOrderDetailActivity.this,
														MyOrderListActivity.class);
												intent.putExtra("order",
														(Serializable) order);

												setResult(RESULT_OK, intent);

												finish();

												reviewDialog.cancel();

												progressDialog.dismiss();
											} else {
												progressDialog.dismiss();

												Toast.makeText(
														MyOrderDetailActivity.this,
														getString(R.string.request_fail)
																+ ":"
																+ result.get("statusMessage"),
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}.execute();
								}
								// }
							});

					Button addCommentCancel = (Button) textEntryView
							.findViewById(R.id.cancel);
					addCommentCancel
							.setOnClickListener(new Button.OnClickListener() {
								@Override
								public void onClick(View v) {
									reviewDialog.cancel();
								}
							});

					reviewDialog.show();
				}
			});

			addCommentButton.setVisibility(View.VISIBLE);
		} else {
			((RatingBar) findViewById(R.id.grade_rating_bar))
					.setRating((Integer) order.get("rate"));

			String comment = (String) order.get("comment");
			if (null != comment && comment.trim().length() > 0) {
				((TextView) findViewById(R.id.review))
						.setText(getString(R.string.review) + "："
								+ order.get("comment"));
				findViewById(R.id.review).setVisibility(View.VISIBLE);
			}

			((LinearLayout) findViewById(R.id.review_layout))
					.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		if (getIntent().getBooleanExtra("return_rent_main", false)) {
			startActivity(new Intent(this, DistributorActivity.class));
		}

		super.finish();
	}

	private void viewOrderDetail(final String orderNo) {
		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.loading), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				new AlertDialog.Builder(MyOrderDetailActivity.this)
						.setIcon(R.drawable.alert)
						.setTitle(R.string.loading)
						.setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int whichButton) {
										MyOrderDetailActivity.this
												.viewOrderDetail(orderNo);
									}
								}).setNegativeButton(R.string.cancel, null)
						.setCancelable(false).show();

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
			protected void onEndpointClientPostExecute(
					final Map<String, Object> result) {
				if (null == result
						|| !StatusCode.SUCCESS.equals((String) result
								.get("statusCode"))) {
					new AlertDialog.Builder(MyOrderDetailActivity.this)
							.setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy)
							.setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											progressDialog.dismiss();

											MyOrderDetailActivity.this
													.viewOrderDetail(orderNo);
										}
									})
							.setNegativeButton(R.string.exit_app,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											exit(orderNo);
										}
									}).setCancelable(true)
							.setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									progressDialog.dismiss();

									MyOrderDetailActivity.this
											.viewOrderDetail(orderNo);
								}
							}).show();

					return;
				}

				setupOrderDetail(result);

				progressDialog.dismiss();
			}
		}.execute();
	}

	public void exit(final String orderNo) {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_menu_more)
				.setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								progressDialog.dismiss();

								new EndpointClient(MyOrderDetailActivity.this) {
									@Override
									protected Map<String, Object> doInBackground(
											Void... arg0) {
										return logout();
									}

									@Override
									protected void onEndpointClientPostExecute(
											Map<String, Object> result) {
										// TODO Auto-generated method stub
									}
								}.execute();

								((ExpandApplication) getApplication())
										.exitApp();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								MyOrderDetailActivity.this
										.viewOrderDetail(orderNo);
							}
						}).setCancelable(false).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}

		if (null != reviewDialog) {
			reviewDialog.dismiss();
		}
	}

}
