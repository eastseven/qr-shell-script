package com.quickride.customer.security.receiver;

import ac.mm.android.util.receiver.SmsReceiver;
import android.content.Context;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-13
 * @version 1.0
 */

public class VerificationSmsReceiver extends SmsReceiver {
	private Context context;

	private TextView verificationCodeText;

	public VerificationSmsReceiver(Context context, TextView verificationCodeText) {
		super();

		this.context = context;
		this.verificationCodeText = verificationCodeText;
	}

	@Override
	public void handleSmsMessage(SmsMessage smsMessage) {
		String sms = smsMessage.getDisplayMessageBody();
		if (smsMessage.getDisplayMessageBody().contains(context.getString(R.string.sms_gateway))) {

			Toast.makeText(context, context.getString(R.string.verification_code_hint) + ": " + sms, Toast.LENGTH_LONG)
					.show();

			verificationCodeText.setText(context.getString(R.string.sms_verification_code) + ": "
					+ smsMessage.getMessageBody());

			// abortBroadcast();
		}
	}
}
