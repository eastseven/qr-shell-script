package com.quickride.customer.trans.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.communication.PhoneUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.MapAbcException;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.route.Route;
import com.quickride.customer.R;
import com.quickride.customer.common.activity.DistributorActivity;
import com.quickride.customer.common.activity.MMapActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.service.PassengerTcpService;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.trans.util.MapAbcUtil;
import com.quickride.customer.trans.view.MyPoiOverlayWithMapAbc;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-15
 * @version 1.0
 */

public class CheckRouteWithMapAbcActivity extends MMapActivity {
	public static String Tag = "CheckRouteActivity";

	private MapView mapView;
	private MapController mapController;

	private MyPoiOverlayWithMapAbc startPoiOverlay;
	private MyPoiOverlayWithMapAbc endPoiOverlay;
	private MyPoiOverlayWithMapAbc carPoiOverlay;
	private GeoPoint startGeoPoint;
	private GeoPoint endGeoPoint;
	private GeoPoint targetGeoPoint;

	private TextView arriveTimeTextView;
	private TextView addressTextView;
	private ImageButton startAddressButton;
	private Button unsubscribeButton;
	private ImageButton endAddressButton;

	private PhoneUtil phoneUtil;

	private Drawable carMarker;

	private ProgressDialog progressDialog;

	private String driverPhone;
	private String carName;
	private String carNumber;
	private String orderNo;
	private String unloadAddress;

	private volatile Integer arriveTime = -1;

	private boolean arrived;
	private boolean loadCustomer;

	private TcpReceiver tcpReceiver;

	private Handler handler;

	private class TcpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(Tag, "BroadcastReceiver Action:" + intent.getAction());

			if (intent.getAction().equals(PassengerTcpService.class.getName())) {
				setupCarPoiItem(intent.getDoubleExtra("carX", 0), intent.getDoubleExtra("carY", 0));

				if (intent.getBooleanExtra("loadCustomer", false)) {
					changeCarryMode();
				}

				if (intent.getBooleanExtra("arrived", false)) {
					arrived = true;

					arriveTimeTextView.setText(R.string.car_arrive);
				}
			}
		}

		private void setupCarPoiItem(double x, double y) {
			if (x == 0 || y == 0) {
				return;
			}

			setCarPoint(x, y);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setMapMode(MAP_MODE_VECTOR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_route_mapabc);

		handler = new Handler();

		arriveTimeTextView = (TextView) findViewById(R.id.arrive_time);
		addressTextView = (TextView) findViewById(R.id.address);

		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(13);

		setTraffic();

		setupCallButton();

		setupCarButton();

		touchStopAnimation();

		setupStartAddressButton();

		unsubscribeButton = (Button) findViewById(R.id.unsubscribe);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (null != startPoiOverlay) {
			startPoiOverlay.removeFromMap();
		}

		if (null != endPoiOverlay) {
			endPoiOverlay.removeFromMap();
		}

		if (null != carPoiOverlay) {
			carPoiOverlay.removeFromMap();
		}

		startGeoPoint = null;
		endGeoPoint = null;
		targetGeoPoint = null;

		arriveTimeTextView.setText("");
		addressTextView.setText("");

		driverPhone = "";
		carName = "";
		carNumber = "";
		orderNo = "";
		unloadAddress = "";

		arriveTime = -1;

		viewOrderDetail();
	}

	private void searchRoute(GeoPoint carPoint) {
		if (null == carPoint || null == targetGeoPoint) {
			return;
		}

		final Route.FromAndTo fromAndTo = new Route.FromAndTo(carPoint, targetGeoPoint);

		handler.post(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<Route> routeResult = Route.calculateRoute(CheckRouteWithMapAbcActivity.this, fromAndTo,
							Route.DrivingDefault);
					if (routeResult != null && !routeResult.isEmpty()) {
						int arriveTime = MapAbcUtil.countConsumeTime(routeResult.get(0));

						if (arriveTime < CheckRouteWithMapAbcActivity.this.arriveTime
								|| -1 == CheckRouteWithMapAbcActivity.this.arriveTime) {
							CheckRouteWithMapAbcActivity.this.arriveTime = arriveTime;

							int arrivalTimePlaces = CheckRouteWithMapAbcActivity.this.arriveTime.toString().length();

							if (arrived) {
								arriveTimeTextView.setText(R.string.car_arrive);

								return;
							}

							if (!loadCustomer) {
								arriveTime *= 2;
							}

							String arrivalTimeString = getString(R.string.estimate_arrive_time, arriveTime);

							SpannableString sp = new SpannableString(arrivalTimeString);
							sp.setSpan(new ForegroundColorSpan(Color.YELLOW), arrivalTimeString.length() - 2
									- arrivalTimePlaces, arrivalTimeString.length() - 2,
									Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
							sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), arrivalTimeString.length() - 2
									- arrivalTimePlaces, arrivalTimeString.length() - 2,
									Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

							arriveTimeTextView.setText(sp);
						}
					}
				} catch (MapAbcException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	private void setupCallButton() {
		phoneUtil = new PhoneUtil(this);

		Button callServiceButton = (Button) findViewById(R.id.call_service);
		callServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(CheckRouteWithMapAbcActivity.this).setIcon(R.drawable.call_contact)
						.setTitle(R.string.customer_service).setMessage(getString(R.string.call_service_message))
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								phoneUtil.call(getString(R.string.service_phone));
							}
						}).setNegativeButton(R.string.no, null).show();
			}
		});

		Button callDriverButton = (Button) findViewById(R.id.call_driver);
		callDriverButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(CheckRouteWithMapAbcActivity.this).setIcon(R.drawable.call_contact)
						.setTitle(R.string.driver)
						.setMessage(String.format(getString(R.string.call_driver_message), driverPhone))
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								phoneUtil.call(driverPhone);
							}
						}).setNegativeButton(R.string.no, null).show();
			}
		});
	}

	private void viewOrderDetail() {
		progressDialog = ProgressDialog.show(this, null, getString(R.string.loading), true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				new AlertDialog.Builder(CheckRouteWithMapAbcActivity.this).setIcon(R.drawable.alert)
						.setTitle(R.string.loading).setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								CheckRouteWithMapAbcActivity.this.viewOrderDetail();
							}
						}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

				return;
			}
		});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return viewOrderDetail(null);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(CheckRouteWithMapAbcActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									progressDialog.dismiss();

									CheckRouteWithMapAbcActivity.this.viewOrderDetail();
								}
							}).setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									exit(true);
								}
							}).setCancelable(true).setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									progressDialog.dismiss();

									CheckRouteWithMapAbcActivity.this.viewOrderDetail();
								}
							}).show();

					return;
				}

				orderNo = (String) result.get("orderNo");
				if (null == orderNo) {
					startActivity(new Intent(CheckRouteWithMapAbcActivity.this, DistributorActivity.class));

					finish();

					return;
				}

				unsubscribeButton.setTag(result);

				driverPhone = (String) result.get("driverMobileNo");
				carName = (String) result.get("carModel");
				carNumber = (String) result.get("licenseNo");

				unloadAddress = (String) result.get("unloadAddress");

				Integer orderStatus = (Integer) result.get("orderStatus");

				setupEndPoint(result);

				if (StatusCode.BOOKED_CAR == orderStatus) {
					if (null != endAddressButton) {
						endAddressButton.setVisibility(View.GONE);
					}
					startAddressButton.setVisibility(View.VISIBLE);
					unsubscribeButton.setEnabled(true);

					setupStartPoint(result);
				} else if (StatusCode.IN_SERVICE == orderStatus) {
					changeCarryMode();
				}

				setCarPoint((Double) result.get("carX"), (Double) result.get("carY"));

				if (null == tcpReceiver) {
					registerReceiver(tcpReceiver = new TcpReceiver(),
							new IntentFilter(PassengerTcpService.class.getName()));
				}

				progressDialog.dismiss();
			}
		}.execute();
	}

	private void changeCarryMode() {
		loadCustomer = true;
		arrived = false;
		arriveTime = -1;

		targetGeoPoint = endGeoPoint;

		if (null != startPoiOverlay) {
			startPoiOverlay.removeFromMap();
		}

		if (null != carPoiOverlay) {
			mapController.animateTo(carPoiOverlay.getItem(0).getPoint());

			carPoiOverlay.showPopupWindow(0);
		}

		startAddressButton.setVisibility(View.GONE);
		unsubscribeButton.setEnabled(false);
		unsubscribeButton.setText("欢迎乘坐");

		setupEndAddressButton();

		arriveTimeTextView.setText("");
		addressTextView.setText("正在前往 " + unloadAddress);
	}

	private void setupEndPoint(final Map<String, ?> result) {
		Drawable marker = getResources().getDrawable(R.drawable.end_point);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		endGeoPoint = new GeoPoint((int) ((Double) result.get("unloadY") * 1E6),
				(int) ((Double) result.get("unloadX") * 1E6));

		List<PoiItem> endPoiItemList = new ArrayList<PoiItem>();
		PoiItem endItem = new PoiItem("end", endGeoPoint, getString(R.string.from_to), "");
		endItem.setTypeDes((String) result.get("unloadAddress"));
		endPoiItemList.add(endItem);

		endPoiOverlay = new MyPoiOverlayWithMapAbc(CheckRouteWithMapAbcActivity.this, marker, endPoiItemList);
		endPoiOverlay.setPopupLayout(R.layout.overlay_popup_simple);
		endPoiOverlay.addToMap(mapView);
	}

	private void setupEndAddressButton() {
		endAddressButton = (ImageButton) findViewById(R.id.get_off_car_location);
		endAddressButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mapController.animateTo(endPoiOverlay.getItem(0).getPoint());

				endPoiOverlay.showPopupWindow(0);
			}
		});

		endAddressButton.setVisibility(View.VISIBLE);
	}

	private void setupStartAddressButton() {
		startAddressButton = (ImageButton) findViewById(R.id.my_location);
		startAddressButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mapController.animateTo(startPoiOverlay.getItem(0).getPoint());

				startPoiOverlay.showPopupWindow(0);
			}
		});
	}

	private void touchStopAnimation() {
		mapView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				mapController.stopAnimation(false);

				return false;
			}
		});
	}

	private void setupCarButton() {
		carMarker = getResources().getDrawable(R.drawable.car_marker);
		carMarker.setBounds(0, 0, carMarker.getIntrinsicWidth(), carMarker.getIntrinsicHeight());

		ImageButton carButton = (ImageButton) findViewById(R.id.car_location);
		carButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (null != carPoiOverlay) {
					mapController.animateTo(carPoiOverlay.getItem(0).getPoint());

					carPoiOverlay.showPopupWindow(0);
				} else {
					Toast.makeText(CheckRouteWithMapAbcActivity.this, "暂时没有获取到车辆位置", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (null != tcpReceiver) {
			unregisterReceiver(tcpReceiver);
		}

		if (null != progressDialog) {
			progressDialog.dismiss();
		}

		carMarker = null;
		carPoiOverlay = null;
		startPoiOverlay = null;
		endPoiOverlay = null;
		mapController = null;
		mapView = null;

		super.onDestroy();
	}

	private void setupStartPoint(Map<String, ?> result) {
		arriveTime = -1;

		if (null != startPoiOverlay) {
			startPoiOverlay.removeFromMap();
		}

		String pickupAddress = (String) result.get("pickupAddress");

		addressTextView.setText(pickupAddress);

		targetGeoPoint = startGeoPoint = new GeoPoint((int) ((Double) result.get("pickupY") * 1E6),
				(int) ((Double) result.get("pickupX") * 1E6));

		Drawable marker = getResources().getDrawable(R.drawable.ic_pickup_marker);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		List<PoiItem> poiItemList = new ArrayList<PoiItem>();
		PoiItem startItem = new PoiItem("start", startGeoPoint, getString(R.string.start_place), "");
		startItem.setTypeDes(pickupAddress);
		poiItemList.add(startItem);

		startPoiOverlay = new MyPoiOverlayWithMapAbc(CheckRouteWithMapAbcActivity.this, marker, poiItemList);
		startPoiOverlay.setPopupLayout(R.layout.overlay_popup_simple);
		startPoiOverlay.addToMap(mapView);
		startPoiOverlay.showPopupWindow(0);

		mapController.animateTo(startGeoPoint);
	}

	private void setCarPoint(double x, double y) {
		// GeoPoint carPoint = new GeoPoint((int) (y * 1E6), (int) (x * 1E6));

		GeoPoint carPoint = null;
		try {
			carPoint = new GeoPoint(y, x, this, getString(R.string.mapabc_key));
		} catch (Exception e) {
			e.printStackTrace();

			return;
		}

		searchRoute(carPoint);

		if (null != carPoiOverlay && null != mapView) {
			carPoiOverlay.removeFromMap();
		}

		List<PoiItem> poiItemList = new ArrayList<PoiItem>();
		PoiItem carItem = new PoiItem("car", carPoint, carName, "");
		carItem.setTypeDes(carNumber);
		poiItemList.add(carItem);

		carPoiOverlay = new MyPoiOverlayWithMapAbc(CheckRouteWithMapAbcActivity.this, carMarker, poiItemList);
		carPoiOverlay.setPopupLayout(R.layout.overlay_popup_simple);
		carPoiOverlay.addToMap(mapView);

		carPoiOverlay.showPopupWindow(0);
	}

	private void setTraffic() {
		ToggleButton trafficToggleButton = new ToggleButton(this);
		trafficToggleButton.setText(getString(R.string.traffic));
		trafficToggleButton.setTextOff(getString(R.string.traffic));
		trafficToggleButton.setTextOn(getString(R.string.traffic));

		trafficToggleButton.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mapView.setVectorMap(false);

					mapView.setTraffic(true);
				} else {
					mapView.setTraffic(false);

					mapView.setVectorMap(true);
				}
			}
		});

		mapView.addView(trafficToggleButton);
	}

	@Override
	public void exit(final Boolean input) {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_menu_more).setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (menuButton.isShowing()) {
							menuButton.dismiss();
						}

						new EndpointClient(CheckRouteWithMapAbcActivity.this) {
							@Override
							protected Map<String, Object> doInBackground(Void... arg0) {
								return logout();
							}

							@Override
							protected void onEndpointClientPostExecute(Map<String, Object> result) {
								// TODO Auto-generated method stub
							}
						}.execute();

						progressDialog.dismiss();

						((ExpandApplication) getApplication()).exitApp();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						if (null != input && true == input) {
							CheckRouteWithMapAbcActivity.this.viewOrderDetail();
						}
					}
				}).setCancelable(false).show();
	}
}
