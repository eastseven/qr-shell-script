package com.quickride.customer.trans.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.location.Address;
import android.util.Log;
import android.util.Pair;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.route.Route;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-3-13
 * @version 1.0
 */

public class MapAbcUtil {
	public static String Tag = "MapAbcUtil";

	private static Pattern hourPattern = Pattern.compile("(\\d+)小时");
	private static Pattern minutesPattern = Pattern.compile("(\\d+)分钟");

	private static int interval = 0;

	public static int countConsumeTime(Route route) {
		int totalTime = 0;
		String consumeTime;

		Matcher hourMatcher;
		Matcher minutesMatcher;
		for (int i = 0; i < route.getStepCount(); i++) {
			consumeTime = route.getStep(i).getConsumeTime();

			hourMatcher = hourPattern.matcher(consumeTime);
			if (hourMatcher.find()) {
				totalTime += Integer.valueOf(hourMatcher.group(1)) * 60;
			}

			minutesMatcher = minutesPattern.matcher(consumeTime);
			if (minutesMatcher.find()) {
				totalTime += Integer.valueOf(minutesMatcher.group(1));
			}

			totalTime += interval;

			Log.d(Tag, "length=" + route.getStep(i).getLength() + " consumeTime=" + consumeTime + " totalTime="
					+ totalTime);
		}

		return totalTime;
	}

	public static boolean isLocationExist(GeoPoint geoPoint) {
		return null != geoPoint && (geoPoint.getLatitudeE6() > 0 && geoPoint.getLongitudeE6() > 0);
	}

	public static Pair<Address, String> getAddress(Geocoder geoCoder, double mlat, double mLon) throws Exception {
		List<Address> address = geoCoder.getFromLocation(mlat, mLon, 3);

		if (null == address || address.isEmpty()) {
			return null;
		}

		Address addres = address.get(0);

		Log.d(Tag,
				"AdminArea=" + addres.getAdminArea() + ", SubAdminArea=" + addres.getSubAdminArea() + ", Locality="
						+ addres.getLocality() + ", SubLocality=" + addres.getSubLocality() + ", FeatureName="
						+ addres.getFeatureName() + ", Thoroughfare=" + addres.getThoroughfare() + ", SubThoroughfare="
						+ addres.getSubThoroughfare());

		String addressName = "";// addres.getAdminArea();
		if (addres.getLocality() != null) {
			addressName += addres.getLocality();
		}

		if (addres.getSubLocality() != null) {
			addressName += addres.getSubLocality();
		}

		if (addres.getFeatureName() != null) {
			addressName += addres.getFeatureName();
		}

		if (addres.getThoroughfare() != null) {
			addressName += addres.getThoroughfare();
		}

		if (addres.getSubThoroughfare() != null) {
			addressName += addres.getSubThoroughfare();
		}

		if (address.size() >= 2) {
			Address poi = address.get(address.size() - 2);
			if (null != poi && null != poi.getFeatureName() && !poi.getFeatureName().equals(addres.getFeatureName())) {
				addressName += "\n[" + poi.getFeatureName() + "附近]";
			}
		}

		return new Pair<Address, String>(addres, addressName);
	}
}
