package com.quickride.customer.payment.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ac.mm.android.activity.BaseActivity;
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
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-19
 * @version 1.0
 */

public class TransferCouponsActivity extends BaseActivity {
	private final static int PICK_CONTACT = 1000;

	// private Validator validator;

	private EditText phoneNoEditText;
	private TextView contactsTextView;

	private PhoneUtil phoneUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.transfer_coupons);

		// validator = new Validator();
		phoneUtil = new PhoneUtil(this);

		final Map<String, Object> coupons = (Map<String, Object>) getIntent().getSerializableExtra("coupons");

		Button contactsButton = (Button) findViewById(R.id.contacts);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

				startActivityForResult(intent, PICK_CONTACT);
			}
		});

		contactsTextView = (TextView) findViewById(R.id.name);

		phoneNoEditText = (EditText) findViewById(R.id.phone);

		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phoneNo = phoneNoEditText.getText().toString();

				if (null == phoneNo || phoneNo.trim().length() == 0) {
					Toast.makeText(TransferCouponsActivity.this, R.string.input_others_phone, Toast.LENGTH_SHORT)
							.show();

					phoneNoEditText.setError(getString(R.string.input_others_phone));

					return;
				}

				// if (!validator.validatePhoneNumber(phoneNo)) {
				// Toast.makeText(TransferCouponsActivity.this, "手机号码验证不合法",
				// Toast.LENGTH_SHORT).show();
				//
				// phoneNoEditText.setError("手机号码验证不合法");
				//
				// return;
				// }

				new AlertDialog.Builder(TransferCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
						.setTitle(R.string.transfer_coupons)
						.setMessage("确定将 " + (String) coupons.get("name") + " 转让给 " + phoneNo + " ？")
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								transferCoupon(phoneNo, (String) coupons.get("couponNo"));
							}
						}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void transferCoupon(final String phoneNo, final String couponNo) {
		final ProgressDialog progressDialog = ProgressDialog.show(TransferCouponsActivity.this, null,
				getString(R.string.waitting), true, true);

		new EndpointClient(TransferCouponsActivity.this) {
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				Map<String, String> transferInfo = new HashMap<String, String>();
				transferInfo.put("couponNo", couponNo);
				transferInfo.put("mobileNo", phoneNo);

				return transferCoupon(transferInfo);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result) {
					new AlertDialog.Builder(TransferCouponsActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									TransferCouponsActivity.this.transferCoupon(phoneNo, couponNo);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									progressDialog.dismiss();

									Toast.makeText(TransferCouponsActivity.this, R.string.request_fail,
											Toast.LENGTH_LONG).show();
								}
							}).setCancelable(false).show();

					return;
				}

				if ("0001".equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(TransferCouponsActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.invite_friend_join)
							.setMessage(phoneNo + " 不是捷乘会员，不能向其转让优惠券。\n立即邀其加入，还可获取奖励喔！")
							.setPositiveButton(R.string.send_invite, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									if (phoneUtil.isAbsentSim()) {
										Toast.makeText(TransferCouponsActivity.this, "无法发送短信，手机无sim卡",
												Toast.LENGTH_LONG).show();

										return;
									}

									final ProgressDialog progressDialog = ProgressDialog.show(
											TransferCouponsActivity.this, null, getString(R.string.waitting), true,
											true);

									phoneUtil.sendSMS(phoneNo, (String) result.get("statusMessage"),
											new SmsSendBackCall() {
												@Override
												public void received() {
													progressDialog.dismiss();

													Toast.makeText(TransferCouponsActivity.this, "对方已接收短信",
															Toast.LENGTH_LONG).show();
												}

												@Override
												public void sendFail() {
													progressDialog.dismiss();

													Toast.makeText(TransferCouponsActivity.this, "短信发送失败",
															Toast.LENGTH_LONG).show();
												}

												@Override
												public void sendSuccess() {
													progressDialog.dismiss();

													Toast.makeText(TransferCouponsActivity.this, "短信发送成功",
															Toast.LENGTH_SHORT).show();
												}
											});
								}
							}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();

					return;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					Toast.makeText(TransferCouponsActivity.this, (String) result.get("statusMessage"),
							Toast.LENGTH_LONG).show();

					return;
				}

				Toast.makeText(TransferCouponsActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();

				Intent intent = new Intent(TransferCouponsActivity.this, MyCouponsActivity.class);
				intent.putExtra("couponNo", couponNo);

				setResult(RESULT_OK, intent);

				finish();
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
							// if (validator.validatePhoneNumber(phoneNumber)) {
							phoneNoEditText.setText(phoneNumber);
							phoneNoEditText.setSelection(phoneNumber.length());
							// } else {
							// phoneNoEditText.setText("");
							//
							// Toast.makeText(TransferCouponsActivity.this,
							// "没有找到 " + name + " 的手机号码",
							// Toast.LENGTH_SHORT).show();
							// }

							return;
						} else {
							// if (validator.validatePhoneNumber(phoneNumber)) {
							phoneNoList.add(phoneNumber);
							// }
						}
					}

					phones.close();

					if (phoneNoList.size() > 1) {
						new AlertDialog.Builder(TransferCouponsActivity.this)
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
						Toast.makeText(TransferCouponsActivity.this, "没有找到 " + name + " 的手机号码", Toast.LENGTH_SHORT)
								.show();
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
