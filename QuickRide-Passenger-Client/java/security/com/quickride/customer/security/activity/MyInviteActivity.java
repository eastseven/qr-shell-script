package com.quickride.customer.security.activity;

import java.util.ArrayList;
import java.util.Map;

import ac.mm.android.util.communication.PhoneUtil;
import ac.mm.android.util.communication.PhoneUtil.SmsSendBackCall;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-7-13
 * @version 1.0
 */

public class MyInviteActivity extends MGestureSwitchPageActivity {
	private final static int PICK_CONTACT = 1000;

	private EditText phoneNoEditText;
	private TextView contactsTextView;
	private ProgressDialog progressDialog;

	private PhoneUtil phoneUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_invite);

		phoneUtil = new PhoneUtil(this);

		Button contactsButton = (Button) findViewById(R.id.contacts);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

				startActivityForResult(intent, PICK_CONTACT);
			}
		});

		phoneNoEditText = (EditText) findViewById(R.id.phone);
		contactsTextView = (TextView) findViewById(R.id.name);

		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (phoneUtil.isAbsentSim()) {
					Toast.makeText(MyInviteActivity.this, "无法发送短信，手机无sim卡", Toast.LENGTH_LONG).show();

					return;
				}

				final String phoneNo = phoneNoEditText.getText().toString();

				if (null == phoneNo || phoneNo.trim().length() == 0) {
					Toast.makeText(MyInviteActivity.this, R.string.input_others_phone, Toast.LENGTH_SHORT).show();

					phoneNoEditText.setError(getString(R.string.input_others_phone));

					return;
				}

				sendInvite(phoneNo);
			}
		});
	}

	private void sendInvite(final String phoneNo) {
		progressDialog = ProgressDialog.show(MyInviteActivity.this, null, getString(R.string.waitting), true, true);

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getInviteNo();
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result) {
					progressDialog.dismiss();

					Toast.makeText(MyInviteActivity.this, "发送失败，请重新发送", Toast.LENGTH_SHORT).show();

					return;
				}

				if (!(Boolean) result.get("success")) {
					progressDialog.dismiss();

					Toast.makeText(MyInviteActivity.this, (String) result.get("message"), Toast.LENGTH_SHORT).show();

					return;
				}

				phoneUtil.sendSMS(phoneNo, (String) result.get("message"), new SmsSendBackCall() {
					@Override
					public void received() {
						progressDialog.dismiss();

						Toast.makeText(MyInviteActivity.this, "对方已接收短信", Toast.LENGTH_LONG).show();
					}

					@Override
					public void sendFail() {
						progressDialog.dismiss();

						Toast.makeText(MyInviteActivity.this, "短信发送失败，请重新发送", Toast.LENGTH_LONG).show();
					}

					@Override
					public void sendSuccess() {
						progressDialog.dismiss();

						Toast.makeText(MyInviteActivity.this, "短信发送成功", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PICK_CONTACT:
			if (resultCode != RESULT_OK) {
				return;
			}

			Uri contactData = data.getData();

			Cursor cursor = managedQuery(contactData, null, null, null, null);

			if (cursor.moveToFirst()) {
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contactsTextView.setText(name);

				String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

				String phoneNumber = null;
				if (hasPhone.equalsIgnoreCase("1")) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
									+ contactData.getLastPathSegment(), null, null);

					final ArrayList<String> phoneNoList = new ArrayList<String>();
					while (phones.moveToNext()) {
						phoneNumber = phones.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

						if (phones.getCount() == 1) {
							phoneNoEditText.setText(phoneNumber);
							phoneNoEditText.setSelection(phoneNumber.length());

							return;
						} else {
							phoneNoList.add(phoneNumber);
						}
					}

					phones.close();

					if (phoneNoList.size() > 1) {
						new AlertDialog.Builder(MyInviteActivity.this)
								.setIcon(R.drawable.ic_menu_more)
								.setTitle("请选择一个手机号码")
								.setSingleChoiceItems(phoneNoList.toArray(new String[0]), 0,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												phoneNoEditText.setText(phoneNoList.get(which));
												phoneNoEditText.setSelection(phoneNoList.get(which).length());

												dialog.dismiss();
											}
										}).setCancelable(false).show();
					} else {
						Toast.makeText(MyInviteActivity.this, "没有找到 " + name + " 的手机号码", Toast.LENGTH_SHORT).show();
					}

				}
			}

			// 不能关闭此游标
			// cursor.close();

			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != phoneUtil) {
			phoneUtil.unregisterSMSReceiver();
		}
	}
}
