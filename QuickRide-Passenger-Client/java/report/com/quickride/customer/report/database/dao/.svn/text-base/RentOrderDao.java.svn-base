package com.quickride.customer.report.database.dao;

import java.util.List;

import android.content.Context;

import com.quickride.customer.common.database.dao.BaseDao;
import com.quickride.customer.report.database.entity.RentOrder;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-2-2
 * @version 1.0
 */

public class RentOrderDao extends BaseDao<RentOrder> {
	public RentOrderDao(Context context) {
		super(context);
	}

	public List<RentOrder> queryAllRentOrderByPage(int pageNumber, int pageItemCount) {
		return queryByPage(null, null, null, null, "orderCarTime DESC", pageNumber, pageItemCount);
	}

	public List<RentOrder> queryWaitExecuteRentOrderByPage(int pageNumber, int pageItemCount) {
		return queryByPage("orderStatus in (?,?)",
				new String[] { String.valueOf(RentOrder.BOOKED_CAR), String.valueOf(RentOrder.SENT_CAR) }, null, null,
				"getOnCarTime", pageNumber, pageItemCount);
	}

	public List<RentOrder> queryInSericeRentOrderByPage(int pageNumber, int pageItemCount) {
		return queryByPage("orderStatus = ?", new String[] { String.valueOf(RentOrder.IN_SERVICE) }, null, null,
				"getOnCarTime", pageNumber, pageItemCount);
	}

	public List<RentOrder> queryFinishedRentOrderByPage(int pageNumber, int pageItemCount) {
		return queryByPage("orderStatus = ?", new String[] { String.valueOf(RentOrder.FINISHED_ORDER) }, null, null,
				"getOffCarTime DESC", pageNumber, pageItemCount);
	}

	public List<RentOrder> queryCanceledRentOrderByPage(int pageNumber, int pageItemCount) {
		return queryByPage("orderStatus in (?,?)",
				new String[] { String.valueOf(RentOrder.CANCELED_ORDER), String.valueOf(RentOrder.UNSUBSCRIBED_CAR) },
				null, null, "orderCarTime DESC", pageNumber, pageItemCount);
	}
}
