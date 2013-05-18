package com.quickride.customer.security.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-17
 * @version 1.0
 */

public class AutoLoginUtil {
	public static final String AUTO_LOGIN = "AUTO_LOGIN";

	public static void enableAutoLogin(Context context) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(AUTO_LOGIN, true);
		editor.commit();
	}

	public static void unableAutoLogin(Context context) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(AUTO_LOGIN, false);
		editor.commit();
	}

	public static boolean isAutoLogin(Context context) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);

		return sharedPreferences.getBoolean(AUTO_LOGIN, false);
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().name,
				Context.MODE_PRIVATE);
		return sharedPreferences;
	}
}
