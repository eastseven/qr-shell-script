package com.quickride.customer.common.database.dao;

import ac.mm.android.database.dao.DatabaseHelper;
import ac.mm.android.database.entity.Entity;
import ac.mm.android.util.date.DateService;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.quickride.customer.R;
import com.quickride.customer.report.database.entity.RentOrder;
import com.quickride.customer.trans.database.entity.Address;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-2-6
 * @version 1.0
 */

public class BaseDao<T extends Entity> extends DatabaseHelper<T> {
	protected DateService dateService;

	public BaseDao(Context context) {
		super(context, context.getString(R.string.database_name), Integer.valueOf(context
				.getString(R.string.database_version)));

		dateService = new DateService();
	}

	@Override
	public void onTransactionCreate(SQLiteDatabase db) {
		super.onTransactionCreate(db);

		createTable(db, RentOrder.class);
		createTable(db, Address.class);

		insertAddress(db, context.getResources().getStringArray(R.array.city_name)[0], "虹桥国际机场 1号航站楼", 31.194012,
				121.348176);
		insertAddress(db, context.getResources().getStringArray(R.array.city_name)[0], "虹桥国际机场 2号航站楼", 31.19457,
				121.32629);
		insertAddress(db, context.getResources().getStringArray(R.array.city_name)[0], "浦东国际机场", 31.15106, 121.80495);
		insertAddress(db, context.getResources().getStringArray(R.array.city_name)[1], "双流国际机场", 30.57970, 103.96066);
	}

	private void insertAddress(SQLiteDatabase db, String city, String title, double latitude, double longitude) {
		Address address = new Address();
		address.setCity(city);
		address.setCreateTime(dateService.now().toLocaleString());
		address.setUpdateTime(dateService.now().toLocaleString());
		address.setFrequency(10);
		address.setLatitudeE6((int) (latitude * 1E6));
		address.setLongitudeE6((int) (longitude * 1E6));
		address.setTitle(title);
		address.setUndeletableType(Address.YES_TYPE);
		address.setCommonAddressType(Address.YES_TYPE);

		insertWithEntity(address, db);
	}
}
