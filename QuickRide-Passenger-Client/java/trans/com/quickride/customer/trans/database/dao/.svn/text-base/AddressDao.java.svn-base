package com.quickride.customer.trans.database.dao;

import java.util.List;

import android.content.Context;

import com.quickride.customer.common.database.dao.BaseDao;
import com.quickride.customer.trans.database.entity.Address;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-27
 * @version 1.0
 */

public class AddressDao extends BaseDao<Address> {
	public AddressDao(Context context) {
		super(context);
	}

	public void insertHistoryAddress(String city, String title, int latitudeE6, int longitudeE6) {
		if (null == city || null == title || "".equals(city.trim()) || "".equals(title.trim())
				|| (latitudeE6 <= 0 && longitudeE6 <= 0)) {
			return;
		}

		Address oldAddress = queryWithLatitudeE6AndLongitudeE6(latitudeE6, longitudeE6);

		if (null != oldAddress) {
			oldAddress.setTitle(title);
			oldAddress.setCity(city);
			oldAddress.setFrequency(oldAddress.getFrequency() + 1);
			oldAddress.setHistoryAddressType(Address.YES_TYPE);
			oldAddress.setUpdateTime(dateService.now().toLocaleString());

			update(oldAddress);

			return;
		}

		Address newAddress = new Address();
		newAddress.setCity(city);
		newAddress.setCreateTime(dateService.now().toLocaleString());
		newAddress.setUpdateTime(dateService.now().toLocaleString());
		newAddress.setFrequency(1);
		newAddress.setLatitudeE6(latitudeE6);
		newAddress.setLongitudeE6(longitudeE6);
		newAddress.setTitle(title);
		newAddress.setHistoryAddressType(Address.YES_TYPE);

		insert(newAddress);
	}

	public List<Address> queryCommonAddressByPageWithCity(String city, int pageNumber, int pageItemCount) {
		return queryByPage(true, "city LIKE ? and commonAddressType=?",
				new String[] { city + "%", String.valueOf(Address.YES_TYPE) }, null, null, "frequency DESC",
				pageNumber, pageItemCount);
	}

	public List<Address> queryHistoryAddressByPageWithCity(String city, int pageNumber, int pageItemCount) {
		return queryByPage(true, "city LIKE ? and historyAddressType=?",
				new String[] { city + "%", String.valueOf(Address.YES_TYPE) }, null, null, "updateTime DESC",
				pageNumber, pageItemCount);
	}

	public Address queryWithLatitudeE6AndLongitudeE6(int latitudeE6, int longitudeE6) {
		return getOnlyEntity(query("latitudeE6=? and longitudeE6=?",
				new String[] { String.valueOf(latitudeE6), String.valueOf(longitudeE6) }, null, null, null));
	}
}
