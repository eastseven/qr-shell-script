package com.quickride.customer.trans.activity;


/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-11-28
 * @version 1.0
 */

public class RentCarWithGoogleMapActivity
// extends MapActivity
{
	// private MapView mapView;
	// private MapController mapController;
	// private MyLocationOverlay myLocationOverlay;
	// private static final int MY_FIX = 1001;
	// private static final int ADDRESS = 1002;
	// private Geocoder geoCoder;
	// private GestureDetector gestureDetector;
	// private MyItemizedOverlay startItemizedOverlay;
	// private TextView addressTextView;
	//
	// private View popView;
	//
	// private Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// if (msg.what == MY_FIX) {
	// mapController.animateTo(myLocationOverlay.getMyLocation());
	// } else if (msg.what == ADDRESS) {
	// addressTextView.setText(msg.obj.toString());
	//
	// if (null != startItemizedOverlay) {
	// startItemizedOverlay.showPopupWindow(0);
	// }
	// }
	// }
	// };
	//
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.rent_main_googlemap);
	//
	// addressTextView = (TextView) findViewById(R.id.address);
	//
	// mapView = (MapView) findViewById(R.id.map);
	// mapView.setOnTouchListener(new View.OnTouchListener() {
	// @Override
	// public boolean onTouch(View view, MotionEvent motionevent) {
	// // mapController.stopAnimation(false);
	//
	// gestureDetector.onTouchEvent(motionevent);
	//
	// return false;
	// }
	// });
	//
	// // google的bug 使用setBuiltInZoomControls会导致与onTouchEvent冲突
	// // mapView.setBuiltInZoomControls(true);
	// ViewGroup zoom = (ViewGroup) findViewById(R.id.zoom);
	// zoom.addView(mapView.getZoomControls());
	//
	// initPopView();
	//
	// mapController = mapView.getController();
	//
	// GeoPoint point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 *
	// 1E6));
	// mapController.setCenter(point);
	// mapController.setZoom(12);
	//
	// geoCoder = new Geocoder(this, Locale.getDefault());
	//
	// myLocationOverlay = new MyLocationOverlay(this, mapView);
	//
	// ImageButton myLocationButton = new ImageButton(this);
	// myLocationButton.setImageResource(R.drawable.my_location);
	// myLocationButton.setPadding(0, 0, 0, 0);
	// myLocationButton.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (myLocationOverlay.isMyLocationEnabled() && null !=
	// myLocationOverlay.getMyLocation()) {
	// GeoPoint geoPoint = myLocationOverlay.getMyLocation();
	// if (null != geoPoint) {
	// setMyAddress();
	// }
	// } else {
	// Toast.makeText(RentCarWithGoogleMapActivity.this,
	// R.string.no_my_location, Toast.LENGTH_SHORT)
	// .show();
	// }
	// }
	// });
	//
	// mapView.addView(myLocationButton, new
	// MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
	// MapView.LayoutParams.WRAP_CONTENT, 0, 0, MapView.LayoutParams.TOP_LEFT));
	//
	// gestureDetector = new GestureDetector(new OnGestureListenerAdpater() {
	// @Override
	// public void onLongPress(MotionEvent motionevent) {
	// if (startItemizedOverlay != null) {
	// startItemizedOverlay.removeOverlay(0);
	// }
	//
	// GeoPoint geoPoint = mapView.getProjection().fromPixels((int)
	// motionevent.getX(),
	// (int) motionevent.getY());
	//
	// Drawable marker = getResources().getDrawable(R.drawable.start_marker);
	// marker.setBounds(0, 0, marker.getIntrinsicWidth(),
	// marker.getIntrinsicHeight());
	//
	// MyOverlayItem startItem = new MyOverlayItem(geoPoint,
	// getString(R.string.pick_me_up), "");
	//
	// startItemizedOverlay = new MyItemizedOverlay(marker, mapView, popView,
	// mapController, R.id.map_bubble,
	// R.id.map_bubble_title, R.id.map_bubble_text);
	//
	// startItemizedOverlay.addOverlay(startItem);
	//
	// mapView.getOverlays().add(startItemizedOverlay);
	//
	// startItemizedOverlay.showPopupWindow(0);
	//
	// setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() /
	// 1E6, startItem,
	// getString(R.string.start_place) + ": ");
	// }
	// });
	//
	// mapView.getOverlays().add(myLocationOverlay);
	//
	// runOnFirstFix();
	// }
	//
	// private void runOnFirstFix() {
	// myLocationOverlay.runOnFirstFix(new Runnable() {
	// public void run() {
	// setMyAddress();
	// }
	// });
	// }
	//
	// private void setMyAddress() {
	// GeoPoint geoPoint = myLocationOverlay.getMyLocation();
	// if (null != geoPoint) {
	// setAddress(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() /
	// 1E6, null,
	// getString(R.string.my_place) + ": ");
	//
	// handler.sendMessage(Message.obtain(handler, MY_FIX));
	// }
	// }
	//
	// @Override
	// protected void onPause() {
	// this.myLocationOverlay.disableMyLocation();
	// // this.locationOverlay.disableCompass();
	//
	// super.onPause();
	// }
	//
	// @Override
	// protected void onResume() {
	// this.myLocationOverlay.enableMyLocation();
	// // this.locationOverlay.enableCompass();
	//
	// super.onResume();
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// }
	//
	// private void setAddress(final double mlat, final double mLon, final
	// MyOverlayItem startItem,
	// final String addressPrefix) {
	// new Thread(new Runnable() {
	// public void run() {
	// getAddress(mlat, mLon, startItem, addressPrefix);
	// }
	// }).start();
	// }
	//
	// public void getAddress(final double mlat, final double mLon, final
	// MyOverlayItem startItem, String addressPrefix) {
	// try {
	// List<Address> address = geoCoder.getFromLocation(mlat, mLon, 1);
	// if (null != address && !address.isEmpty()) {
	// Address addres = address.get(0);
	// String addressName = addres.getAdminArea();
	// if (addres.getLocality() != null) {
	// addressName += addres.getLocality();
	// }
	//
	// if (addres.getSubLocality() != null) {
	// addressName += addres.getSubLocality();
	// }
	//
	// addressName += addres.getFeatureName();
	//
	// if (null != startItem) {
	// startItem.setTypeDes(addressName);
	// }
	// System.out.println("addressName=" + addressName);
	// handler.sendMessage(Message.obtain(handler, ADDRESS, addressPrefix +
	// addressName));
	// }
	// } catch (Exception igore) {
	// }
	// }
	//
	// @Override
	// protected boolean isRouteDisplayed() {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// private void initPopView() {
	// if (null == popView) {
	// popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
	// mapView.addView(popView, new
	// MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
	// MapView.LayoutParams.WRAP_CONTENT, null,
	// MapView.LayoutParams.BOTTOM_CENTER));
	// popView.setVisibility(View.GONE);
	// }
	// }
}
