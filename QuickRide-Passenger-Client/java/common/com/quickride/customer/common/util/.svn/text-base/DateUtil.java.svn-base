package com.quickride.customer.common.util;

import java.util.Calendar;

import android.content.Context;

import com.quickride.customer.R;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-3-17
 * @version 1.0
 */

public class DateUtil {
	private Context context;

	public DateUtil(Context context) {
		this.context = context;
	}

	public String getDateString(long millisecond) {
		String dateString = "";
		if (0 == millisecond) {
			dateString = context.getString(R.string.now);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(millisecond);

			dateString = calendar.getTime().toLocaleString();
		}

		return dateString;
	}
}
