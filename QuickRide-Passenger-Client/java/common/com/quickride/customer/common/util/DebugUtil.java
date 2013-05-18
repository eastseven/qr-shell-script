package com.quickride.customer.common.util;

import java.util.ArrayList;
import java.util.Map;

import android.util.Log;

public final class DebugUtil {

	private static final String tag = "DebugUtil";
	
	public static void print(Map<String, ?> map, Class<?> clazz) {
		String name = clazz.getName();
		Log.d(tag, name);
		
		if(map == null) {
			Log.d(tag, "map is null");
			return;
		}
		
		if(map.isEmpty()) {
			Log.d(tag, "map is empty");
			return;
		}
		
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if(value instanceof ArrayList<?>) {
				Log.d(tag, key + "=====");
				ArrayList<?> result = (ArrayList<?>) value;
				for (Object sub : result) {
					Log.d(tag, sub + "["+sub.getClass()+"]");
				}
			} else {
				Log.d(tag, key + "=" + value + "["+value.getClass()+"]");
			}
		}
	}
}
