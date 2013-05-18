package com.quickride.customer.trans.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.listener.OnGestureListenerAdpater;
import ac.mm.android.map.SerializablePoiItem;
import ac.mm.android.util.sensor.MSensorManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.PoiOverlay;
import com.mapabc.mapapi.map.RouteMessageHandler;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.route.Route;
import com.quickride.customer.R;
import com.quickride.customer.common.activity.MMapActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.trans.domain.RentCarStatus;
import com.quickride.customer.trans.util.MapAbcUtil;
import com.quickride.customer.trans.view.MLocationOverlay;
import com.quickride.customer.trans.view.MyPoiOverlayWithMapAbc;
import com.quickride.customer.trans.view.RectBoxOverlay;

/**
 * ��˵����
 * 
 * @author WPM
 * @date 2011-11-18
 * @version 1.0
 */

public class RentCarWithMapAbcActivity extends MMapActivity implements RouteMessageHandler {
	public static String Tag = "RentCarWithMapAbcActivity";

	private static final int MY_FIX = 1001;
	private static final int ADDRESS = 1002;
	private static final int IO_EXCEPTION = 1003;
	private static final int ROUTE_SEARCH = 1004;
	private static final int FAST_ARRIVE_TIME = 1005;

	private MapView mapView;
	private MapController mapController;

	private MLocationOverlay myLocationOverlay;
	private PoiOverlay carPoiOverlay;
	private PoiOverlay startPoiOverlay;
	private PoiOverlay endPoiOverlay;

	private Geocoder geoCoder;
	private GestureDetector gestureDetector;
	private Button choseCityButton;
	private Button setPlaceButton;
	private Button resetPoiButton;
	private TextView searchText;
	private RelativeLayout topLayout;
	private LinearLayout rootLayout;
	private LinearLayout routeLayout;
	private TextView arriveTimeTextView;

	private ProgressDialog progressDialog;
	private AlertDialog resetPoiAlertDialog;
	private AlertDialog setStartPlaceDialog;
	private AlertDialog setGpsDialog;

	private StartPlaceButtonOnClickListener startPlaceButtonOnClickListener;
	private EndPlaceButtonOnClickListener endPlaceButtonOnClickListener;
	private RentTimeButtonOnClickListener timeButtonOnClickListener;
	private ResetPoiButtonOnClickListener resetPoiButtonOnClickListener;

	private GeoPoint shangHaiPoint;
	private GeoPoint chengDuPoint;

	private RouteOverlay routeOverlay;

	private SerializablePoiItem startPoiItem;
	private SerializablePoiItem endPoiItem;

	private List<Map<String, Object>> rangeList;

	private int mileage;
	private int expectedConsumeTime;
	private String serviceTime;

	private volatile RentCarStatus rentCarStatus;

	private ImageView km_one;
	private ImageView km_two;
	private ImageView km_three;
	private ImageView hours_one;
	private ImageView hours_two;
	private ImageView minutes_one;
	private ImageView minutes_two;

	private int[] numImageIds;

	private Drawable carMarker;
	private Drawable startMarker;
	private Drawable endMarker;

	private Vibrator vibrator;
	private MSensorManager shakeManager;

	private EndpointClient getAroundCarsEndpointClient;

	private Handler handler;

	private LocationManager locationManager;

	private class MHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}

			switch (msg.what) {
			case MY_FIX:
				GeoPoint myLocation = myLocationOverlay.getMyLocation();

				setPoint(myLocation);

				myLocationOverlay.showPop();

				if (null != startPoiOverlay) {
					startPoiOverlay.closePopupWindow();
				}

				if (null != endPoiOverlay) {
					endPoiOverlay.closePopupWindow();
				}

				break;

			case ADDRESS:
				Pair<RentCarStatus, String> addressPair = (Pair<RentCarStatus, String>) msg.obj;

				if (RentCarStatus.set_start_point_process == addressPair.first) {
					if (null != startPoiOverlay) {
						startPoiOverlay.showPopupWindow(0);
					}
				} else if (RentCarStatus.set_end_point_process == addressPair.first) {
					if (null != endPoiOverlay) {
						endPoiOverlay.showPopupWindow(0);
					}
				}

				break;
			case FAST_ARRIVE_TIME:
				Integer arrivalTime = (Integer) msg.obj;

				arrivalTime *= 2;

				if (arrivalTime < 5) {
					arrivalTime = 5;
				}

				int arrivalTimePlaces = arrivalTime.toString().length();

				String arrivalTimeString = getString(R.string.fastest_arrive_time, arrivalTime);

				SpannableString sp = new SpannableString(arrivalTimeString);
				sp.setSpan(new ForegroundColorSpan(Color.YELLOW), arrivalTimeString.length() - 2 - arrivalTimePlaces,
						arrivalTimeString.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), arrivalTimeString.length() - 2
						- arrivalTimePlaces, arrivalTimeString.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

				arriveTimeTextView.setText(sp);

				break;
			case IO_EXCEPTION:
				Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();

				((ExpandApplication) getApplication()).getNetworkUtil().handleConnectException();

				break;
			case ROUTE_SEARCH:
				if (routeOverlay != null) {
					routeOverlay.removeFromMap(mapView);
				}

				// if (startPoiOverlay != null) {
				// startPoiOverlay.removeFromMap();
				// }
				//
				// if (endPoiOverlay != null) {
				// endPoiOverlay.removeFromMap();
				// }

				Route route = ((List<Route>) msg.obj).get(0);

				routeOverlay = new RouteOverlay(RentCarWithMapAbcActivity.this, route);
				routeOverlay.registerRouteMessage(RentCarWithMapAbcActivity.this);
				routeOverlay.enableDrag(false);
				Paint paint = new Paint();
				paint.setColor(Color.BLUE);
				paint.setAntiAlias(true);
				paint.setStrokeWidth(6);
				routeOverlay.setCarLinePaint(paint);
				routeOverlay.addToMap(mapView);

				int km = (int) Math.ceil(route.getLength() / 1000.0);
				Log.d(Tag, "route.getLength=" + route.getLength() + "m ≈" + km + "km");

				mileage = km;

				showMileage(km);

				showConsumeTime(route);

				progressDialog.dismiss();

				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setMapMode(MAP_MODE_VECTOR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rent_main_mapabc);

		setTitle(R.string.setup_start_place);

		rentCarStatus = RentCarStatus.set_start_point_process;

		handler = new MHandler();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
		topLayout = (RelativeLayout) findViewById(R.id.rent_main_top_layout);
		arriveTimeTextView = (TextView) findViewById(R.id.arrive_time);

		startPoiItem = new SerializablePoiItem();
		endPoiItem = new SerializablePoiItem();

		geoCoder = new Geocoder(this);

		shangHaiPoint = new GeoPoint((int) (31.24169 * 1E6), (int) (121.49491 * 1E6));
		chengDuPoint = new GeoPoint((int) (30.6583 * 1E6), (int) (104.0660 * 1E6));

		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);

		setTraffic();

		mapController = mapView.getController();
		mapController.setZoom(12);

		setupChoseCityButton();

		setPoint(shangHaiPoint);

		myLocationOverlay = new MLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		setupMyLocationButton();

		setupPlaceButton();

		LinearLayout search = (LinearLayout) findViewById(R.id.search);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RentCarWithMapAbcActivity.this, SearchAddressWithMapAbcActivity.class);
				intent.putExtra("city", choseCityButton.getText());
				intent.putExtra("rentCarStatus", rentCarStatus);

				startActivityForResult(intent, 0);

				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		searchText = (TextView) findViewById(R.id.search_text);

		startMarker = getResources().getDrawable(R.drawable.start_point);
		startMarker.setBounds(0, 0, startMarker.getIntrinsicWidth(), startMarker.getIntrinsicHeight());
		endMarker = getResources().getDrawable(R.drawable.end_point);
		endMarker.setBounds(0, 0, endMarker.getIntrinsicWidth(), endMarker.getIntrinsicHeight());
		carMarker = getResources().getDrawable(R.drawable.car_marker);
		carMarker.setBounds(0, 0, carMarker.getIntrinsicWidth(), carMarker.getIntrinsicHeight());

		getEnabledRange();

		handleLongPressEvent();

		runOnFirstFix();

		setupShake();

		checkGPS();
	}

	private void checkGPS() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			new AlertDialog.Builder(RentCarWithMapAbcActivity.this).setIcon(R.drawable.alert).setTitle("您还没有开启GPS")
					.setMessage("请开启GPS定位，爱订车将为您提供更好的服务")
					.setPositiveButton("前往开启", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							gotoSettingPage();
						}
					}).setNegativeButton(R.string.cancel, null).show();
		}
	}

	private void setupShake() {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		shakeManager = new MSensorManager(this);
		shakeManager.setOnShakeListener(new MSensorManager.OnShakeListener() {
			@Override
			public void onShake() {
				if (rentCarStatus == RentCarStatus.set_start_point_process) {
					if (null == setStartPlaceDialog || !setStartPlaceDialog.isShowing()) {
						vibrator.vibrate(100);

						if (null == startPoiOverlay) {
							GeoPoint myPoint = myLocationOverlay.getMyLocation();

							if (MapAbcUtil.isLocationExist(myPoint)) {
								setupRideCarPoiOverlay(myPoint, "", RentCarStatus.set_start_point_process);

								Toast.makeText(RentCarWithMapAbcActivity.this, R.string.pick_me_up, Toast.LENGTH_SHORT)
										.show();
							} else {
								handleUnableLocate();
							}
						} else {
							startPoiOverlay.removeFromMap();
							mapView.invalidate();

							startPoiOverlay = null;

							setPlaceButton.setText(getString(R.string.setup_start_place));

							Toast.makeText(RentCarWithMapAbcActivity.this, R.string.setup_start_place,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else if (rentCarStatus == RentCarStatus.set_end_point_process) {
					if (null != endPoiOverlay) {
						vibrator.vibrate(100);

						endPoiOverlay.removeFromMap();
						mapView.invalidate();

						endPoiOverlay = null;

						Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_end_place, Toast.LENGTH_SHORT)
								.show();
					} else {
						if (null == resetPoiAlertDialog || !resetPoiAlertDialog.isShowing()) {
							vibrator.vibrate(100);

							showResetPoiAlertDialog();
						}
					}
				} else if (rentCarStatus == RentCarStatus.plan_route_process) {
					if (null == resetPoiAlertDialog || !resetPoiAlertDialog.isShowing()) {
						vibrator.vibrate(100);

						showResetPoiAlertDialog();
					}
				}
			}
		});
	}

	private void getEnabledRange() {
		progressDialog = ProgressDialog.show(RentCarWithMapAbcActivity.this, null, getString(R.string.loading), true,
				true, new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						exit(null);
					}
				});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getEnabledRange();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result || !(Boolean) result.get("success")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RentCarWithMapAbcActivity.this)
							.setIcon(R.drawable.alert).setTitle(R.string.server_busy)
							.setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									RentCarWithMapAbcActivity.this.getEnabledRange();
								}
							}).setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									exit(null);
								}
							}).setCancelable(true);

					alertDialogBuilder.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							RentCarWithMapAbcActivity.this.getEnabledRange();
						}
					});

					alertDialogBuilder.show();

					return;
				}

				Map<String, Object> message = (Map<String, Object>) result.get("message");

				// String timeRange = (String) message.get("time");
				// if (null != timeRange && timeRange.trim().length() > 0) {
				// new
				// AlertDialog.Builder(RentCarWithMapAbcActivity.this).setMessage(timeRange)
				// .setPositiveButton(R.string.confirm, null).show();
				// }

				serviceTime = (String) message.get("time");

				rangeList = (List<Map<String, Object>>) message.get("range");
				for (Map<String, Object> range : rangeList) {
					if (0 == (Integer) range.get("type")) {
						RectBoxOverlay rectBoxOverlay = new RectBoxOverlay(new GeoPoint(
								(int) ((Double) range.get("leftBottomY") * 1E6),
								(int) ((Double) range.get("leftBottomX") * 1E6)), new GeoPoint(
								(int) ((Double) range.get("rightTopY") * 1E6),
								(int) ((Double) range.get("rightTopX") * 1E6)), Color.BLUE);

						mapView.getOverlays().add(rectBoxOverlay);
					}
				}
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			SerializablePoiItem serializablePoiItem = (SerializablePoiItem) data
					.getSerializableExtra("SerializablePoiItem");

			if (RentCarStatus.set_start_point_process == rentCarStatus) {
				startPoiItem.setTitle(serializablePoiItem.getTitle());
				startPoiItem.setCity(serializablePoiItem.getCity());
			} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
				endPoiItem.setTitle(serializablePoiItem.getTitle());
				endPoiItem.setCity(serializablePoiItem.getCity());
			}

			setupRideCarPoiOverlay(
					new GeoPoint(serializablePoiItem.getLatitudeE6(), serializablePoiItem.getLongitudeE6()),
					serializablePoiItem.getTitle(), rentCarStatus);

			break;
		}
	}

	private void setTraffic() {
		if (MAP_MODE_BITMAP == getMapMode()) {
			CheckBox traffic = new CheckBox(this);
			traffic.setText(R.string.traffic);
			traffic.setTextColor(Color.MAGENTA);
			traffic.setTypeface(null, Typeface.BOLD);
			traffic.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						mapView.setTraffic(true);
					} else {
						mapView.setTraffic(false);
					}
				}
			});

			mapView.addView(traffic);
		}
	}

	private void setPoint(GeoPoint point) {
		mapController.animateTo(point, new Runnable() {
			@Override
			public void run() {
				setCity();
			}
		});
	}

	private void handleLongPressEvent() {
		gestureDetector = new GestureDetector(new OnGestureListenerAdpater() {
			@Override
			public void onLongPress(MotionEvent motionevent) {
				GeoPoint geoPoint = mapView.getProjection().fromPixels((int) motionevent.getX(),
						(int) motionevent.getY());

				setupRideCarPoiOverlay(geoPoint, "", rentCarStatus);
			}
		});

		mapView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				mapController.stopAnimation(false);

				gestureDetector.onTouchEvent(motionevent);

				return false;
			}
		});
	}

	private void setupRideCarPoiOverlay(GeoPoint geoPoint, String address, RentCarStatus rentCarStatus) {
		if (!isServiceArea(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, rentCarStatus)) {
			return;
		}

		myLocationOverlay.dismissPop();

		if (RentCarStatus.set_start_point_process == rentCarStatus) {
			if (startPoiOverlay != null) {
				startPoiOverlay.removeFromMap();
			}

			List<PoiItem> poiItemList = new ArrayList<PoiItem>();
			PoiItem startItem = new PoiItem("start", geoPoint, getString(R.string.pick_me_up), "");
			startItem.setTypeDes(address);
			poiItemList.add(startItem);

			startPoiOverlay = new MyPoiOverlayWithMapAbc(RentCarWithMapAbcActivity.this, startMarker, poiItemList) {
				@Override
				protected View.OnClickListener getOnClickBubbleListener() {
					return startPlaceButtonOnClickListener;
				}
			};

			startPoiOverlay.addToMap(mapView);
			startPoiOverlay.showPopupWindow(0);

			setPlaceButton.setText(getString(R.string.pick_me_up));

			// setPoint(geoPoint);

			if (null == address || "".equals(address)) {
				setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, startItem,
						RentCarStatus.set_start_point_process);
			}

			getAroundCars(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6);
		} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
			if (endPoiOverlay != null) {
				endPoiOverlay.removeFromMap();
			}

			List<PoiItem> poiItemList = new ArrayList<PoiItem>();
			PoiItem endItem = new PoiItem("end", geoPoint, getString(R.string.from_to), "");
			endItem.setTypeDes(address);
			poiItemList.add(endItem);

			endPoiOverlay = new MyPoiOverlayWithMapAbc(RentCarWithMapAbcActivity.this, endMarker, poiItemList) {
				@Override
				protected View.OnClickListener getOnClickBubbleListener() {
					return endPlaceButtonOnClickListener;
				}
			};
			endPoiOverlay.addToMap(mapView);
			endPoiOverlay.showPopupWindow(0);

			// setPoint(geoPoint);

			if (null == address || "".equals(address)) {
				setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, endItem,
						RentCarStatus.set_end_point_process);
			}
		}
	}

	private String getStartPlacePrefix() {
		return getString(R.string.start_place) + ": ";
	}

	private String getEndPlacePrefix() {
		return getString(R.string.end_place) + ": ";
	}

	private void setupChoseCityButton() {
		choseCityButton = (Button) findViewById(R.id.city);
		choseCityButton.setText(getResources().getStringArray(R.array.city_name)[0]);
		choseCityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int checked = -1;

				if (getResources().getStringArray(R.array.city_name)[0].equals(choseCityButton.getText())) {
					checked = 0;
				} else if (getResources().getStringArray(R.array.city_name)[1].equals(choseCityButton.getText())) {
					checked = 1;
				}

				AlertDialog choseCityDialog = new AlertDialog.Builder(RentCarWithMapAbcActivity.this)
						.setIcon(R.drawable.ic_menu_more)
						.setTitle(R.string.chose_city)
						.setSingleChoiceItems(getResources().getStringArray(R.array.city_name), checked,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										String city = getResources().getStringArray(R.array.city_name)[which];

										choseCityButton.setText(city);

										dialog.dismiss();

										switch (which) {
										case 0:
											setPoint(shangHaiPoint);
											break;
										case 1:
											setPoint(chengDuPoint);
											break;
										}
									}
								}).create();

				choseCityDialog.setCanceledOnTouchOutside(true);

				choseCityDialog.show();
			}
		});
	}

	private void setupMyLocationButton() {
		ImageButton myLocationButton = (ImageButton) findViewById(R.id.my_location);
		myLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GeoPoint geoPoint = myLocationOverlay.getMyLocation();

				Log.d(Tag, "isMyLocationEnabled=" + myLocationOverlay.isMyLocationEnabled() + " geoPoint=" + geoPoint);

				if (MapAbcUtil.isLocationExist(geoPoint)) {
					setMyAddress();
				} else {
					handleUnableLocate();
				}
			}
		});
	}

	private void handleUnableLocate() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (null == setGpsDialog) {
				setGpsDialog = new AlertDialog.Builder(RentCarWithMapAbcActivity.this).setIcon(R.drawable.alert)
						.setTitle(R.string.no_my_location).setMessage("请开启GPS网络定位")
						.setPositiveButton("前往开启", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// startActivity(new
								// Intent(Settings.ACTION_SECURITY_SETTINGS));
								gotoSettingPage();
							}

						}).setNegativeButton(R.string.cancel, null).show();
			} else if (!setGpsDialog.isShowing()) {
				setGpsDialog.show();
			}
		} else {
			Toast.makeText(RentCarWithMapAbcActivity.this, R.string.no_my_location, Toast.LENGTH_SHORT).show();
		}

		enableMyLocation();
	}

	private void enableMyLocation() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (!myLocationOverlay.isMyLocationEnabled()) {
				try {
					myLocationOverlay.enableMyLocation();
				} catch (Exception e) {
					e.fillInStackTrace();
				}
			}
		} else {
			disableMyLocation();
		}
	}

	private void disableMyLocation() {
		if (myLocationOverlay.isMyLocationEnabled()) {
			myLocationOverlay.disableMyLocation();
		}
	}

	private void gotoSettingPage() {
		try {
			Intent intent = new Intent("/");
			ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
			intent.setComponent(cm);
			intent.setAction("android.intent.action.VIEW");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();

			Toast.makeText(this, "无法打开设置页面！您可以自行前往设置页面开启GPS定位服务", Toast.LENGTH_LONG).show();
		}
	}

	private void setupPlaceButton() {
		setPlaceButton = (Button) findViewById(R.id.setup_ride_place);
		setPlaceButton
				.setOnClickListener(null == startPlaceButtonOnClickListener ? startPlaceButtonOnClickListener = new StartPlaceButtonOnClickListener()
						: startPlaceButtonOnClickListener);
	}

	private class StartPlaceButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			setStartPlace();
		}
	}

	private class ResetPoiButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			showResetPoiAlertDialog();
		}
	}

	private void setStartPlace() {
		if (rentCarStatus == RentCarStatus.set_end_point_process) {
			Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_end_place, Toast.LENGTH_SHORT).show();
		}

		if (rentCarStatus != RentCarStatus.set_start_point_process) {
			return;
		}

		String message;
		final GeoPoint myPoint = myLocationOverlay.getMyLocation();

		if (null != startPoiOverlay) {
			message = getString(R.string.confirm_chose_start_place_message);

			mapController.setCenter(startPoiOverlay.getItem(0).getPoint());
		} else if (MapAbcUtil.isLocationExist(myPoint)) {
			message = getString(R.string.confirm_my_start_place_message);

			setupRideCarPoiOverlay(myPoint, "", RentCarStatus.set_start_point_process);
		} else {
			Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_start_place, Toast.LENGTH_SHORT).show();

			return;
		}

		showSetStartPlaceDialog(message);
	}

	private void showSetStartPlaceDialog(String message) {
		if (null == startPoiOverlay) {
			return;
		}

		if (null == startPoiItem.getTitle() || "".equals(startPoiItem.getTitle().trim())) {
			progressDialog = ProgressDialog.show(this, null, "正在获取乘车地址，请稍后重试...", true, true);

			GeoPoint geoPoint = startPoiOverlay.getItem(0).getPoint();

			setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, startPoiOverlay.getItem(0),
					RentCarStatus.set_start_point_process);

			return;
		}

		if (null == setStartPlaceDialog) {
			setStartPlaceDialog = new AlertDialog.Builder(RentCarWithMapAbcActivity.this)
					.setIcon(R.drawable.start_point).setTitle(R.string.pick_me_up).setMessage(message)
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (rentCarStatus == RentCarStatus.set_end_point_process) {
								return;
							}

							rentCarStatus = RentCarStatus.set_end_point_process;

							setTitle(R.string.setup_end_place);

							startPoiItem.setLatitudeE6(startPoiOverlay.getItem(0).getPoint().getLatitudeE6());
							startPoiItem.setLongitudeE6(startPoiOverlay.getItem(0).getPoint().getLongitudeE6());

							setPlaceButton.setText(R.string.setup_end_place);
							setPlaceButton
									.setOnClickListener(null == endPlaceButtonOnClickListener ? endPlaceButtonOnClickListener = new EndPlaceButtonOnClickListener()
											: endPlaceButtonOnClickListener);

							searchText.setHint(R.string.search_from_end);

							// choseCityButton.setEnabled(false);

							if (null == resetPoiButton) {
								resetPoiButton = new Button(RentCarWithMapAbcActivity.this);
								resetPoiButton.setText(R.string.reset_place);
								resetPoiButton.getBackground().setAlpha(200);
								resetPoiButton.setTypeface(null, Typeface.BOLD);
								resetPoiButton
										.setOnClickListener(null == resetPoiButtonOnClickListener ? resetPoiButtonOnClickListener = new ResetPoiButtonOnClickListener()
												: resetPoiButtonOnClickListener);
							}
							mapView.addView(resetPoiButton);

							if (null == endPoiOverlay) {
								setPoint(new GeoPoint(startPoiItem.getLatitudeE6(), startPoiItem.getLongitudeE6()));
							} else {
								GeoPoint geoPoint = endPoiOverlay.getItem(0).getPoint();

								// setPoint(geoPoint);

								String address = endPoiOverlay.getItem(0).getTypeDes();

								if (null == address || "".equals(address.trim())) {
									setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6,
											endPoiOverlay.getItem(0), RentCarStatus.set_end_point_process);
								} else {
									// endPoiOverlay.showPopupWindow(0);
								}
							}

							Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_end_place, Toast.LENGTH_SHORT)
									.show();
						}
					}).setNegativeButton(R.string.cancel, null).show();
		} else if (!setStartPlaceDialog.isShowing()) {
			setStartPlaceDialog.setMessage(message);
			setStartPlaceDialog.show();
		}
	}

	private void showResetPoiAlertDialog() {
		if (null == resetPoiAlertDialog) {
			resetPoiAlertDialog = new AlertDialog.Builder(RentCarWithMapAbcActivity.this).setIcon(R.drawable.alert)
					.setTitle(R.string.reset_place_title).setMessage(R.string.confirm_reset_place_message)
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (rentCarStatus == RentCarStatus.set_start_point_process) {
								return;
							}

							rentCarStatus = RentCarStatus.set_start_point_process;

							setTitle(R.string.setup_start_place);

							mapView.removeView(resetPoiButton);

							setPlaceButton.setText(R.string.pick_me_up);
							setPlaceButton.setOnClickListener(startPlaceButtonOnClickListener);

							searchText.setHint(R.string.search_from_start);

							// choseCityButton.setEnabled(true);

							if (null != routeLayout) {
								routeLayout.setVisibility(View.GONE);
							}

							topLayout.setVisibility(View.VISIBLE);

							if (routeOverlay != null) {
								routeOverlay.removeFromMap(mapView);
							}

							if (null != endPoiOverlay) {
								endPoiOverlay.closePopupWindow();

								endPoiOverlay.removeFromMap();
								mapView.invalidate();

								endPoiOverlay = null;
							}

							if (null != startPoiOverlay) {
								startPoiOverlay.closePopupWindow();

								startPoiOverlay.removeFromMap();
								mapView.invalidate();

								startPoiOverlay = null;
							}

							// if (null == startPoiOverlay) {
							setMyAddress();
							// } else {
							// GeoPoint geoPoint =
							// startPoiOverlay.getItem(0).getPoint();
							//
							// setPoint(geoPoint);
							//
							// String address =
							// startPoiOverlay.getItem(0).getTypeDes();
							//
							// if (null == address || "".equals(address.trim()))
							// {
							// setAddress(geoPoint.getLatitudeE6() / 1E6,
							// geoPoint.getLongitudeE6() / 1E6,
							// startPoiOverlay.getItem(0),
							// RentCarStatus.set_start_point_process);
							// } else {
							// startPoiOverlay.showPopupWindow(0);
							// }
							// }

							Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_start_place,
									Toast.LENGTH_SHORT).show();
						}
					}).setNegativeButton(R.string.cancel, null).show();
		} else if (!resetPoiAlertDialog.isShowing()) {
			resetPoiAlertDialog.show();
		}
	}

	private class RentTimeButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			if (0 == mileage || 0 == expectedConsumeTime) {
				searchRoute(new GeoPoint(startPoiItem.getLatitudeE6(), startPoiItem.getLongitudeE6()), endPoiOverlay
						.getItem(0).getPoint());

				return;
			}

			Intent intent = new Intent(RentCarWithMapAbcActivity.this, SetRentTimeActivity.class);
			intent.putExtra("startPoiItem", startPoiItem);
			intent.putExtra("endPoiItem", endPoiItem);

			if (null != myLocationOverlay) {
				GeoPoint myLocation = myLocationOverlay.getMyLocation();
				if (MapAbcUtil.isLocationExist(myLocation)) {
					SerializablePoiItem myPoiItem = new SerializablePoiItem();
					myPoiItem.setLatitudeE6(myLocation.getLatitudeE6());
					myPoiItem.setLongitudeE6(myLocation.getLongitudeE6());

					intent.putExtra("myPoiItem", myPoiItem);
				}
			}

			intent.putExtra("mileage", mileage);
			intent.putExtra("expectedConsumeTime", expectedConsumeTime);
			intent.putExtra("serviceTime", serviceTime);

			startActivity(intent);
		}
	}

	private class EndPlaceButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			if (rentCarStatus == RentCarStatus.set_start_point_process) {
				Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_start_place, Toast.LENGTH_SHORT).show();
			}

			if (rentCarStatus != RentCarStatus.set_end_point_process) {
				return;
			}

			if (null != endPoiOverlay) {
				mapController.setCenter(endPoiOverlay.getItem(0).getPoint());

				if (null == endPoiItem.getTitle() || "".equals(endPoiItem.getTitle().trim())) {
					progressDialog = ProgressDialog.show(RentCarWithMapAbcActivity.this, null, "正在获取下车地址，请稍后重试...",
							true, true);

					GeoPoint geoPoint = endPoiOverlay.getItem(0).getPoint();

					setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6,
							endPoiOverlay.getItem(0), RentCarStatus.set_end_point_process);

					return;
				}

				new AlertDialog.Builder(RentCarWithMapAbcActivity.this).setTitle(R.string.setup_end_place)
						.setIcon(R.drawable.end_point).setMessage(R.string.confirm_chose_end_place_message)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								if (rentCarStatus == RentCarStatus.plan_route_process) {
									return;
								}

								rentCarStatus = RentCarStatus.plan_route_process;

								setTitle(R.string.planning_route);

								setPlaceButton.setText(R.string.next);
								setPlaceButton
										.setOnClickListener(null == timeButtonOnClickListener ? timeButtonOnClickListener = new RentTimeButtonOnClickListener()
												: timeButtonOnClickListener);

								if (null != startPoiOverlay) {
									startPoiOverlay.closePopupWindow();
								}
								endPoiOverlay.closePopupWindow();

								topLayout.setVisibility(View.GONE);

								if (null == routeLayout) {
									routeLayout = (LinearLayout) getLayoutInflater()
											.inflate(R.layout.route_count, null);

									km_one = (ImageView) routeLayout.findViewById(R.id.km_one);
									km_two = (ImageView) routeLayout.findViewById(R.id.km_two);
									km_three = (ImageView) routeLayout.findViewById(R.id.km_three);
									hours_one = (ImageView) routeLayout.findViewById(R.id.hours_one);
									hours_two = (ImageView) routeLayout.findViewById(R.id.hours_two);
									minutes_one = (ImageView) routeLayout.findViewById(R.id.minutes_one);
									minutes_two = (ImageView) routeLayout.findViewById(R.id.minutes_two);

									numImageIds = new int[] { R.drawable.num_0, R.drawable.num_1, R.drawable.num_2,
											R.drawable.num_3, R.drawable.num_4, R.drawable.num_5, R.drawable.num_6,
											R.drawable.num_7, R.drawable.num_8, R.drawable.num_9 };

									rootLayout.addView(routeLayout, 0);
								}
								hours_one.setImageResource(numImageIds[0]);
								hours_two.setImageResource(numImageIds[0]);
								minutes_one.setImageResource(numImageIds[0]);
								minutes_two.setImageResource(numImageIds[0]);

								km_one.setImageResource(numImageIds[0]);
								km_two.setImageResource(numImageIds[0]);
								km_three.setImageResource(numImageIds[0]);

								routeLayout.setVisibility(View.VISIBLE);

								endPoiItem.setLatitudeE6(endPoiOverlay.getItem(0).getPoint().getLatitudeE6());
								endPoiItem.setLongitudeE6(endPoiOverlay.getItem(0).getPoint().getLongitudeE6());

								searchRoute(new GeoPoint(startPoiItem.getLatitudeE6(), startPoiItem.getLongitudeE6()),
										endPoiOverlay.getItem(0).getPoint());

								setPoint(endPoiOverlay.getItem(0).getPoint());
							}
						}).setNegativeButton(R.string.cancel, null).show();
			} else {
				Toast.makeText(RentCarWithMapAbcActivity.this, R.string.chose_end_place, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void runOnFirstFix() {
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				if (null == startPoiOverlay) {
					setMyAddress();
				}
			}
		});
	}

	private void setMyAddress() {
		GeoPoint geoPoint = myLocationOverlay.getMyLocation();

		if (MapAbcUtil.isLocationExist(geoPoint)) {
			if (null == startPoiOverlay) {
				getAroundCars(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6);
			}

			handler.sendMessage(Message.obtain(handler, MY_FIX));
		}
	}

	@Override
	protected void onPause() {
		disableMyLocation();

		// this.locationOverlay.disableCompass();

		if (null != shakeManager) {
			shakeManager.stop();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		enableMyLocation();

		// this.locationOverlay.enableCompass();

		super.onResume();

		if (null != shakeManager) {
			shakeManager.start();
		}
	}

	private void getAroundCars(final double mlat, final double mLon) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				arriveTimeTextView.setText("");

				if (null != getAroundCarsEndpointClient && !getAroundCarsEndpointClient.isCancelled()) {
					getAroundCarsEndpointClient.cancel(true);
				}

				getAroundCarsEndpointClient = (EndpointClient) new EndpointClient(RentCarWithMapAbcActivity.this) {
					@Override
					protected Map<String, Object> doInBackground(Void... arg0) {
						Map<String, String> location = new HashMap<String, String>();
						location.put("pickupY", String.valueOf(mlat));
						location.put("pickupX", String.valueOf(mLon));

						return getAroundCars(location);
					}

					@Override
					protected void onEndpointClientPostExecute(Map<String, Object> result) {
						if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
							return;
						}

						final List<Map<String, Double>> carList = (List<Map<String, Double>>) result.get("carPoints");

						if (null == carList || carList.isEmpty()) {
							if (null != carPoiOverlay && null != mapView) {
								carPoiOverlay.removeFromMap();
							}

							return;
						}

						if (null != carPoiOverlay && null != mapView) {
							carPoiOverlay.removeFromMap();
						}

						List<PoiItem> poiItemList = new ArrayList<PoiItem>();

						for (Map<String, Double> car : carList) {
							// GeoPoint carGeoPoint = new GeoPoint((int)
							// (car.get("y") * 1E6), (int) (car.get("x") *
							// 1E6));

							GeoPoint carGeoPoint = null;
							try {
								carGeoPoint = new GeoPoint(car.get("y"), car.get("x"), RentCarWithMapAbcActivity.this,
										getString(R.string.mapabc_key));
							} catch (Exception e) {
								e.printStackTrace();

								continue;
							}

							PoiItem carItem = new PoiItem("", carGeoPoint, "", "");
							poiItemList.add(carItem);
						}

						try {
							carPoiOverlay = new PoiOverlay(carMarker, poiItemList);
							carPoiOverlay.enablePopup(false);
							carPoiOverlay.addToMap(mapView);
						} catch (Exception e) {
							e.printStackTrace();
						}

						final EndpointClient endpointClient = this;

						new Thread(new Runnable() {
							@Override
							public void run() {
								GeoPoint pickupGeoPoint = new GeoPoint((int) (mlat * 1E6), (int) (mLon * 1E6));

								int fastArriveTime = -1;

								for (Map<String, Double> car : carList) {
									GeoPoint carGeoPoint = new GeoPoint((int) (car.get("y") * 1E6),
											(int) (car.get("x") * 1E6));

									final Route.FromAndTo fromAndTo = new Route.FromAndTo(carGeoPoint, pickupGeoPoint);

									try {
										List<Route> routeResult = Route.calculateRoute(RentCarWithMapAbcActivity.this,
												fromAndTo, Route.DrivingDefault);
										if (routeResult != null && !routeResult.isEmpty()) {
											int arriveTime = MapAbcUtil.countConsumeTime(routeResult.get(0));

											if (arriveTime < fastArriveTime || -1 == fastArriveTime) {
												fastArriveTime = arriveTime;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();

										return;
									}
								}

								if (null != endpointClient && getAroundCarsEndpointClient == endpointClient
										&& !endpointClient.isCancelled()) {
									handler.sendMessage(Message.obtain(handler, FAST_ARRIVE_TIME, fastArriveTime));
								}
							}
						}).start();
					}
				}.execute();
			}
		});
	}

	private void setAddress(final double mlat, final double mLon, final PoiItem poiItem,
			final RentCarStatus rentCarStatus) {
		if (RentCarStatus.set_start_point_process == rentCarStatus) {
			startPoiItem.setTitle("");
			startPoiItem.setCity("");
		} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
			endPoiItem.setTitle("");
			endPoiItem.setCity("");
		}

		new Thread(new Runnable() {
			public void run() {
				getAddress(mlat, mLon, poiItem, rentCarStatus);
			}
		}).start();
	}

	public void getAddress(final double mlat, final double mLon, final PoiItem poiItem, RentCarStatus rentCarStatus) {
		if (0 >= mlat && 0 >= mLon) {
			return;
		}

		try {
			Pair<Address, String> address = MapAbcUtil.getAddress(geoCoder, mlat, mLon);

			if (null == address) {
				if (null != poiItem) {
					poiItem.setTypeDes(getString(R.string.click_get_address));
				}

				return;
			}

			String addressString = address.second;
			Address addres = address.first;

			if (null != poiItem) {
				poiItem.setTypeDes(addressString);
			}

			String addressPrefix = "";

			if (RentCarStatus.set_start_point_process == rentCarStatus) {
				startPoiItem.setTitle(addressString);

				if (null != addres.getLocality()) {
					startPoiItem.setCity(addres.getLocality());
				} else if (null != addres.getAdminArea()) {
					startPoiItem.setCity(addres.getAdminArea());
				}

				addressPrefix = getStartPlacePrefix();
			} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
				endPoiItem.setTitle(addressString);

				if (null != addres.getLocality()) {
					endPoiItem.setCity(addres.getLocality());
				} else if (null != addres.getAdminArea()) {
					endPoiItem.setCity(addres.getAdminArea());
				}

				addressPrefix = getEndPlacePrefix();
			} else if (RentCarStatus.set_my_address_status == rentCarStatus) {
				addressPrefix = getString(R.string.my_place) + ": ";
			}

			handler.sendMessage(Message.obtain(handler, ADDRESS, new Pair<RentCarStatus, String>(rentCarStatus,
					addressPrefix + address)));
		} catch (Exception e) {
			e.printStackTrace();

			handler.sendMessage(Message.obtain(handler, IO_EXCEPTION));
		} finally {
			if (null != progressDialog) {
				progressDialog.dismiss();
			}
		}
	}

	private void setCity() {
		if (((ExpandApplication) getApplication()).isExitedApp() || isFinishing() || null == mapView) {
			return;
		}

		double latitude = mapView.getMapCenter().getLatitudeE6() / 1E6;
		double longitude = mapView.getMapCenter().getLongitudeE6() / 1E6;

		if (isChengDu(latitude, longitude)) {
			choseCityButton.setText(getResources().getStringArray(R.array.city_name)[1]);
		} else if (isShangHai(latitude, longitude)) {
			choseCityButton.setText(getResources().getStringArray(R.array.city_name)[0]);
		}
	}

	private boolean isServiceArea(double latitude, double longitude, RentCarStatus rentCarStatus) {
		// if (null == rangeList || rangeList.size() == 0) {
		// return true;
		// }

		String description = " ";

		for (Map<String, Object> range : rangeList) {
			if (RentCarStatus.set_start_point_process == rentCarStatus) {
				if (0 == (Integer) range.get("type")) {
					if (isInRange(latitude, longitude, range)) {
						return true;
					}

					description += range.get("description") + " ";
				}
			} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
				if (1 == (Integer) range.get("type")) {
					if (isInRange(latitude, longitude, range)) {
						return true;
					}

					description += range.get("description") + " ";
				}
			}
		}

		if (RentCarStatus.set_start_point_process == rentCarStatus) {
			Toast.makeText(RentCarWithMapAbcActivity.this, getString(R.string.no_service_area_start, description),
					Toast.LENGTH_LONG).show();
		} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
			Toast.makeText(RentCarWithMapAbcActivity.this, getString(R.string.no_service_area_end, description),
					Toast.LENGTH_LONG).show();

			for (Map<String, Object> range : rangeList) {
				if (1 == (Integer) range.get("type")) {
					RectBoxOverlay rectBoxOverlay = new RectBoxOverlay(new GeoPoint(
							(int) ((Double) range.get("leftBottomY") * 1E6),
							(int) ((Double) range.get("leftBottomX") * 1E6)), new GeoPoint(
							(int) ((Double) range.get("rightTopY") * 1E6),
							(int) ((Double) range.get("rightTopX") * 1E6)), Color.RED);

					mapView.getOverlays().add(rectBoxOverlay);
				}
			}
		}

		return false;
	}

	private boolean isInRange(double latitude, double longitude, Map<String, Object> range) {
		return latitude < (Double) range.get("rightTopY") && latitude > (Double) range.get("leftBottomY")
				&& longitude > (Double) range.get("leftBottomX") && longitude < (Double) range.get("rightTopX");
	}

	private boolean isShangHai(double latitude, double longitude) {
		return 30.63 < latitude && latitude < 31.88 && 120.16 < longitude && longitude < 122.53;
	}

	private boolean isChengDu(double latitude, double longitude) {
		return 30.03 < latitude && latitude < 31.31 && 102.72 < longitude && longitude < 105.20;
	}

	private void searchRoute(GeoPoint startPoint, GeoPoint endPoint) {
		mileage = 0;
		expectedConsumeTime = 0;

		progressDialog = ProgressDialog.show(this, null, getString(R.string.planning_route), true, true);

		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint, endPoint);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<Route> routeResult = Route.calculateRoute(RentCarWithMapAbcActivity.this, fromAndTo,
							Route.DrivingDefault);
					if (progressDialog.isShowing()) {
						if (routeResult != null && !routeResult.isEmpty())
							handler.sendMessage(Message.obtain(handler, ROUTE_SEARCH, routeResult));
					}
				} catch (Exception e) {
					e.printStackTrace();

					handler.sendMessage(Message.obtain(handler, IO_EXCEPTION));

					progressDialog.dismiss();
				}
			}
		}).start();
	}

	private void showConsumeTime(Route route) {
		int totalTime = MapAbcUtil.countConsumeTime(route);

		expectedConsumeTime = totalTime;

		int hoursBit1 = totalTime / 60 % 10;
		int hourskmBit10 = totalTime / 60 / 10 % 10;
		int minutesBit1 = totalTime % 60 % 10;
		int minutesBit10 = totalTime % 60 / 10 % 10;

		hours_one.setImageResource(numImageIds[hourskmBit10]);
		hours_two.setImageResource(numImageIds[hoursBit1]);
		minutes_one.setImageResource(numImageIds[minutesBit10]);
		minutes_two.setImageResource(numImageIds[minutesBit1]);
	}

	private void showMileage(int km) {
		// 个位 十位 百位
		int kmBit1 = km % 10;
		int kmBit10 = km / 10 % 10;
		int kmBit100 = km / 100 % 10;

		km_one.setImageResource(numImageIds[kmBit100]);
		km_two.setImageResource(numImageIds[kmBit10]);
		km_three.setImageResource(numImageIds[kmBit1]);
		// km_one.setImageResource(R.drawable.m8);
		// km_two.setImageResource(R.drawable.m8);
		// km_three.setImageResource(R.drawable.m8);
	}

	@Override
	public void onDrag(MapView mapview, RouteOverlay routeoverlay, int i, GeoPoint geopoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragBegin(MapView mapview, RouteOverlay routeoverlay, int i, GeoPoint geopoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragEnd(MapView mapview, RouteOverlay routeoverlay, int i, GeoPoint geopoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onRouteEvent(MapView mapview, RouteOverlay routeoverlay, int i, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exit(Boolean input) {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_menu_more).setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (menuButton.isShowing()) {
							menuButton.dismiss();
						}

						new EndpointClient(RentCarWithMapAbcActivity.this) {
							@Override
							protected Map<String, Object> doInBackground(Void... arg0) {
								return logout();
							}

							@Override
							protected void onEndpointClientPostExecute(Map<String, Object> result) {
								// TODO Auto-generated method stub
							}
						}.execute();

						((ExpandApplication) getApplication()).exitApp();
					}
				}).setNegativeButton(R.string.no, null).setCancelable(false).show();
	}

	@Override
	protected void onDestroy() {
		if (null != progressDialog) {
			progressDialog.dismiss();
		}

		if (null != getAroundCarsEndpointClient && !getAroundCarsEndpointClient.isCancelled()) {
			getAroundCarsEndpointClient.cancel(true);
		}

		myLocationOverlay = null;
		carMarker = null;
		startMarker = null;
		endMarker = null;
		carPoiOverlay = null;
		startPoiOverlay = null;
		endPoiOverlay = null;
		geoCoder = null;
		mapController = null;
		mapView = null;

		super.onDestroy();
	}
}
