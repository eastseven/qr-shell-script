package com.quickride.customer.common.domain;

public enum BusinessCode {

	MO_CONNECT_CUSTOMER_REQ(0x0001, true),

	MO_CONNECT_CUSTOMER_RESP(0x5001, false),

	MO_PULSE_CUSTOMER_REQ(0x0002, true),

	MO_PULSE_CUSTOMER_RESP(0x5002, false),

	MT_NOTIFY_RIDE_REQ(0x0003, true),

	MT_NOTIFY_RIDE_RESP(0x5003, false),

	MT_UPDATE_ROUTE_REQ(0x0004, true),

	MT_UPDATE_ROUTE_RESP(0x5004, false),

	MT_NOTIFY_DISPATCH_REQ(0x0007, true),

	MT_NOTIFY_DISPATCH_RESP(0x5007, false),

	MO_CONNECT_DRIVER_REQ(0x1001, true),

	MT_LOAD_CUSTOMER_REQ(0x0005, true),

	MT_LOAD_CUSTOMER_RESP(0x5005, false),

	MT_UNLOAD_CUSTOMER_REQ(0x0006, false),

	MT_UNLOAD_CUSTOMER_RESP(0x5006, false),

	MO_CONNECT_DRIVER_RESP(0x6001, false),

	MO_UPLOAD_POSITION_REQ(0x1003, true),

	MO_UPLOAD_POSITION_RESP(0x6003, false),

	MT_NOTIFY_PICKUP_LEASE_REQ(0x1004, true),

	MT_NOTIFY_PICKUP_LEASE_RESP(0x6004, false),

	MT_NOTIFY_PICKUP_TAXI_REQ(0x1005, true),

	MT_NOTIFY_PICKUP_TAXI_RESP(0x6005, false),

	MO_ERROR_RESP(0x9999, false);

	private int code;

	private boolean isMo;

	BusinessCode(int code, boolean isMo) {
		this.code = code;
		this.isMo = isMo;
	}

	public int getCode() {
		return code;
	}

	public boolean isMo() {
		return isMo;
	}

	public boolean isMt() {
		return !isMo;
	}

	public static BusinessCode codeOf(int code) {
		BusinessCode[] ids = BusinessCode.values();
		BusinessCode result = null;
		for (BusinessCode businessCode : ids) {
			if (businessCode.getCode() == code) {
				result = businessCode;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("not exist BusinessCode");
		}
		return result;
	}
}
