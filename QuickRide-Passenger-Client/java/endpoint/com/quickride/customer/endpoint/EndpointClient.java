package com.quickride.customer.endpoint;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.remote.http.client.SimpleHttpClient;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.security.activity.LoginActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-5
 * @version 1.0
 */

public abstract class EndpointClient extends AsyncTask<Void, Void, Map<String, Object>> {
	public static String Tag = "EndpointClient";

	private static String FINISHED = "FINISHED";

	private Context context;

	private SimpleHttpClient httpClient;

	public EndpointClient(Context context) {
		this.context = context;

		httpClient = ((ExpandApplication) context.getApplicationContext()).getDefaultSimpleHttpClient();
	}

	protected Map<String, Object> regist(Map<String, String> registInfo) {
		registInfo.put("imsi", getIMSI());

		return httpClient.fetchJsonObject(context.getString(R.string.domain) + context.getString(R.string.regist_url),
				registInfo);
	}

	protected Map<String, Object> updateApp() {
		Map<String, String> updateInfo = new HashMap<String, String>();
		updateInfo.put("packageName", context.getPackageName());
		updateInfo.put("platform", "Android");
		updateInfo.put("platformApiVersion", String.valueOf(android.os.Build.VERSION.SDK_INT));

		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);

			updateInfo.put("versionCode", String.valueOf(pi.versionCode));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.update_app_url), updateInfo);
	}

	protected Map<String, Object> login(Map<String, String> loginInfo) {
		return httpClient.fetchJsonObject(context.getString(R.string.domain) + context.getString(R.string.login_url),
				loginInfo);
	}

	protected Map<String, Object> logout() {
		return httpClient.fetchJsonObject(context.getString(R.string.domain) + context.getString(R.string.logout_url),
				null);
	}

	protected Map<String, Object> getEnabledRange() {
		return httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_enabled_range_url), null);
	}

	protected Map<String, Object> getRentStatus() {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_rent_status_url), null));
	}

	protected Map<String, Object> getInviteNo() {
		return httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_invite_no_url), null);
	}

	protected Map<String, Object> getCaptcha() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("captcha", httpClient.fetchBytes(
				context.getString(R.string.domain) + context.getString(R.string.get_captcha_url), null));

		return result;
	}

	protected Map<String, Object> getSmsCode(String phoneNumber) {
		return httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_verification_code_url)
						+ phoneNumber, null);
	}

	protected Map<String, Object> getUser() {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_user_url), null));
	}

	protected Map<String, Object> updateUser(Map<String, String> user) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.update_user_url), user));
	}

	protected Map<String, Object> modifyPassword(Map<String, String> passwordInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.modify_password_url), passwordInfo));
	}

	protected Map<String, Object> sendActiveEmail(Map<String, String> email) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.send_active_email_url), email));
	}

	protected Map<String, Object> findPassword(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.find_password_url), info));
	}

	protected Map<String, Object> getAroundCars(Map<String, String> location) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_around_cars_url), location));
	}

	public Map<String, Object> requestLeaseCar(Map<String, String> requestLeaseCarInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.request_lease_car_url),
				requestLeaseCarInfo));
	}

	protected Map<String, Object> selectLeaseCar(Map<String, String> selectLeaseCarInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.select_lease_car_url),
				selectLeaseCarInfo));
	}

	protected Map<String, Object> viewOrderDetail(Map<String, String> orderId) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.view_order_detail_url), orderId));
	}

	protected Map<String, Object> unsubscribe(Map<String, String> unsubscribeInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.unsubscribe_url), unsubscribeInfo));
	}

	protected Map<String, Object> getUnsubscribePrompt(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_unsubscribe_prompt_url), info));
	}

	public Map<String, Object> listOrders(Map<String, String> listOrdersInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.list_orders_url), listOrdersInfo));
	}

	protected Map<String, Object> rateDriver(Map<String, String> rate) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.rate_driver_url), rate));
	}

	protected Map<String, Object> selectPayType(Map<String, String> payType) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.select_pay_type_url), payType));
	}

	// protected Map<String, Object> pay(Map<String, String> couponId) {
	// return handleResponseResult(httpClient.fetchJsonObject(
	// context.getString(R.string.domain) + context.getString(R.string.pay_url),
	// couponId));
	// }

	protected Map<String, Object> transferCoupon(Map<String, String> transferInfo) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.transfer_coupon_url), transferInfo));
	}

	protected Map<String, Object> getCoupon(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_coupons_url), info));
	}

	protected Map<String, Object> exchangeCoupon(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.exchange_coupons_url), info));
	}

	protected Map<String, Object> getEnableCoupons(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_enable_coupons_url), info));
	}

	public Map<String, Object> getMyCoupons(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_my_coupons_url), info));
	}

	public Map<String, Object> getAllCoupons(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_all_coupons_url), info));
	}

	public Map<String, Object> getPointsLog(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_points_log_url), info));
	}

	public Map<String, Object> getAllCampaign(Map<String, String> info) {
		return handleResponseResult(httpClient.fetchJsonObject(
				context.getString(R.string.domain) + context.getString(R.string.get_all_campaign_url), info));
	}

	public String getIMSI() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getSubscriberId();
	}

	/**
	 * 车辆档次
	 * @param params 无
	 * @return
	 */
	public Map<String, Object> getCarGrades(Map<String, String> params) {
		return handleResponseResult(httpClient.fetchJsonObject(context.getString(R.string.domain) + context.getString(R.string.get_car_grade_list_url), params));
	}
	
	/**
	 * 特价产品
	 * @param params : pickupTime(yyyy-mm-dd hh:mm:ss), pickupAddress or dischargeAddress, carGrade 都是string
	 * @return
	 */
	public Map<String, Object> getOrderProducts(Map<String, String> params) {
		return handleResponseResult(httpClient.fetchJsonObject(context.getString(R.string.domain) + context.getString(R.string.get_order_product_list_url), params));
	}
	
	/* --------------------------------------------------------------------------------------------- */
	
	private Map<String, Object> handleResponseResult(Map<String, Object> result) {
		if (null != result && StatusCode.USER_NOT_LOGIN.equals((String) result.get("statusCode"))) {
			context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			result.put(FINISHED, true);
		}

		return result;
	}

	@Override
	protected void onPostExecute(Map<String, Object> result) {
		if (null != result && null != result.get(EndpointClient.FINISHED)
				&& (Boolean) result.get(EndpointClient.FINISHED)) {
			Toast.makeText(context, context.getString(R.string.relogin_message), Toast.LENGTH_LONG).show();

			return;
		}

		if (context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return;
			}
		}

		onEndpointClientPostExecute(result);
	}

	protected abstract void onEndpointClientPostExecute(Map<String, Object> result);
}
