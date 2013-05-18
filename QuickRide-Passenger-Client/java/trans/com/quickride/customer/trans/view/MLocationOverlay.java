package com.quickride.customer.trans.view;

import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.Projection;
import com.quickride.customer.R;
import com.quickride.customer.common.activity.MMapActivity;
import com.quickride.customer.trans.util.MapAbcUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-31
 * @version 1.0
 */

public class MLocationOverlay extends MyLocationOverlay {
	private View mPopView = null;

	private MapView mapView = null;

	private volatile boolean isFirstFix = false;

	private volatile boolean isDisplay = false;

	private TextView addressTextView;

	private Context context;

	private Geocoder geoCoder;

	public MLocationOverlay(final MMapActivity context, MapView mapView) {
		super(context, mapView);

		this.context = context;
		this.mapView = mapView;

		geoCoder = new Geocoder(context);

		mPopView = LayoutInflater.from(context).inflate(R.layout.overlay_popup_simple, null);
		dismissPop();

		TextView titleTextView = (TextView) mPopView.findViewById(R.id.map_bubble_title);
		addressTextView = (TextView) mPopView.findViewById(R.id.map_bubble_text);
		titleTextView.setText(context.getString(R.string.my_place));
		// addressTextView.setText(address);

		mPopView.setOnClickListener(getOnClickBubbleListener());

		mapView.addView(mPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
	}

	public void dismissPop() {
		mPopView.setVisibility(View.GONE);

		isDisplay = false;
	}

	public void showPop() {
		updatePopLocation();

		mPopView.setVisibility(View.VISIBLE);

		isDisplay = true;
	}

	private void updatePopLocation() {
		mapView.updateViewLayout(mPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, getMyLocation(), MapView.LayoutParams.BOTTOM_CENTER));
	}

	protected View.OnClickListener getOnClickBubbleListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				reverseGeocode();
			}
		};
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);

		if (null == location) {
			return;
		}

		addressTextView.setText(context.getString(R.string.click_get_address));

		if (!isFirstFix) {
			showPop();

			reverseGeocode();

			isFirstFix = true;
		}

		if (isDisplay) {
			updatePopLocation();
		}
	}

	protected void reverseGeocode() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return getAddress();
			}

			@Override
			protected void onPostExecute(String result) {
				if (null != result && result.trim().length() > 0) {
					addressTextView.setText(result);
				} else {
					addressTextView.setText(context.getString(R.string.click_get_address));
				}
			}
		}.execute();

		addressTextView.setText(context.getString(R.string.loading));
	}

	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapview) {
		Projection projection = mapview.getProjection();
		Point point = projection.toPixels(geoPoint, null);

		GeoPoint myLocation = getMyLocation();
		if (null == myLocation) {
			dismissPop();

			return super.onTap(geoPoint, mapview);
		}

		Point locationPoint = projection.toPixels(myLocation, null);

		if (Math.abs(point.x - locationPoint.x) < 13 && Math.abs(point.y - locationPoint.y) < 13) {
			showPop();
		} else {
			dismissPop();
		}

		return super.onTap(geoPoint, mapview);
	}

	private String getAddress() {
		GeoPoint myLocation = getMyLocation();
		if (null == myLocation || !MapAbcUtil.isLocationExist(myLocation)) {
			return null;
		}

		try {
			Pair<Address, String> address = MapAbcUtil.getAddress(geoCoder, myLocation.getLatitudeE6() / 1E6,
					myLocation.getLongitudeE6() / 1E6);

			if (null == address) {
				return null;
			}

			return address.second;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
