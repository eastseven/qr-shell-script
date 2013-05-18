package com.quickride.customer.security.manager;

import java.util.Map;

import ac.mm.core.manager.Manager;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.service.CountDownService;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-13
 * @version 1.0
 */

public class GetVerificationSmsManager implements Manager<GetVerificationSmsBusinessContext, Void> {
	@Override
	public Void execute(GetVerificationSmsBusinessContext businessContext) {
		businessContext.getGetVerificationSmsButton().setEnabled(false);

		businessContext.getContext().startService(new Intent(businessContext.getContext(), CountDownService.class));

		getSmsCode(businessContext);

		return null;
	}

	private void getSmsCode(final GetVerificationSmsBusinessContext businessContext) {
		new EndpointClient(businessContext.getContext()) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getSmsCode(businessContext.getPhoneNumber());
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result || !(Boolean) result.get("success")) {
					businessContext.getContext().stopService(
							new Intent(businessContext.getContext(), CountDownService.class));

					Toast.makeText(businessContext.getContext(), "暂时无法获取短信验证码，请稍后再试", Toast.LENGTH_LONG).show();

					new Handler().postDelayed(new Runnable() {
						public void run() {
							businessContext.getGetVerificationSmsButton().setText(R.string.get_sms_code);
							businessContext.getGetVerificationSmsButton().setEnabled(true);
						}
					}, 2000);
				}
			}
		}.execute();
	}
}
