package com.quickride.customer.trans.database.entity;

import ac.mm.android.database.entity.Entity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-27
 * @version 1.0
 */

public class Address extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8437020027644733257L;

	public static final int NO_TYPE = 0;
	public static final int YES_TYPE = 1;

	private String title;
	private int longitudeE6;
	private int latitudeE6;
	private int frequency;
	private int status;
	private String createTime;
	private String updateTime;
	private String city;
	private int historyAddressType;
	private int commonAddressType;
	private int undeletableType;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLongitudeE6() {
		return longitudeE6;
	}

	public void setLongitudeE6(int longitudeE6) {
		this.longitudeE6 = longitudeE6;
	}

	public int getLatitudeE6() {
		return latitudeE6;
	}

	public void setLatitudeE6(int latitudeE6) {
		this.latitudeE6 = latitudeE6;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getHistoryAddressType() {
		return historyAddressType;
	}

	public void setHistoryAddressType(int historyAddressType) {
		this.historyAddressType = historyAddressType;
	}

	public int getCommonAddressType() {
		return commonAddressType;
	}

	public void setCommonAddressType(int commonAddressType) {
		this.commonAddressType = commonAddressType;
	}

	public int getUndeletableType() {
		return undeletableType;
	}

	public void setUndeletableType(int undeletableType) {
		this.undeletableType = undeletableType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Address [title=" + title + ", longitudeE6=" + longitudeE6 + ", latitudeE6=" + latitudeE6
				+ ", frequency=" + frequency + ", status=" + status + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", city=" + city + ", historyAddressType=" + historyAddressType + ", commonAddressType="
				+ commonAddressType + ", undeletableType=" + undeletableType + "]";
	}
}
