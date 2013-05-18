package com.quickride.customer.trans.view;

import java.util.ArrayList;
import java.util.List;

import ac.mm.android.util.communication.PhoneUtil;
import ac.mm.android.view.adapter.ListAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.WebViewActivity;
import com.quickride.customer.common.domain.MenuExitable;
import com.quickride.customer.payment.activity.MarketingCampaignActivity;
import com.quickride.customer.payment.activity.MyCouponsActivity;
import com.quickride.customer.payment.activity.MyPointsActivity;
import com.quickride.customer.report.activity.MyOrderListActivity;
import com.quickride.customer.security.activity.AccountManagementActivity;

/**
 * ��˵����
 * 
 * @author WPM
 * @date 2011-11-18
 * @version 1.0
 */

public class MenuGridView extends GridView {
	public MenuGridView(Context context) {
		this(context, null);
	}

	public MenuGridView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// setSelector(new ColorDrawable(Color.TRANSPARENT));
		setNumColumns(4);
		setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		setVerticalSpacing(30);
		// setHorizontalSpacing(0);
		setPadding(0, 30, 0, 0);
		setGravity(Gravity.CENTER);

		List<View> itemViewList = new ArrayList<View>();

		TextView account = new TextView(context);
		account.setGravity(Gravity.CENTER);
		account.setTextColor(getResources().getColor(R.color.white));
		account.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.account, 0, 0);
		account.setText(R.string.account_management);
		account.setSingleLine();
		account.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, AccountManagementActivity.class);

				context.startActivity(intent);
			}
		});

		TextView myOrder = new TextView(context);
		myOrder.setGravity(Gravity.CENTER);
		myOrder.setTextColor(getResources().getColor(R.color.white));
		myOrder.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_paste_holo_dark, 0, 0);
		myOrder.setText(R.string.my_order);
		myOrder.setSingleLine();
		myOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, MyOrderListActivity.class);

				context.startActivity(intent);
			}
		});

		TextView myPoints = new TextView(context);
		myPoints.setGravity(Gravity.CENTER);
		myPoints.setTextColor(getResources().getColor(R.color.white));
		myPoints.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sym_keyboard_shift_locked, 0, 0);
		myPoints.setText(R.string.my_points);
		myPoints.setSingleLine();
		myPoints.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, MyPointsActivity.class);

				context.startActivity(intent);
			}
		});

		TextView myCoupons = new TextView(context);
		myCoupons.setGravity(Gravity.CENTER);
		myCoupons.setTextColor(getResources().getColor(R.color.white));
		myCoupons.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.coupon, 0, 0);
		myCoupons.setText(R.string.my_coupons);
		myCoupons.setSingleLine();
		myCoupons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, MyCouponsActivity.class);

				context.startActivity(intent);
			}
		});

		TextView marketingCampaign = new TextView(context);
		marketingCampaign.setGravity(Gravity.CENTER);
		marketingCampaign.setTextColor(getResources().getColor(R.color.white));
		marketingCampaign.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.campaign, 0, 0);
		marketingCampaign.setText(R.string.marketing_campaign);
		marketingCampaign.setSingleLine();
		marketingCampaign.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, MarketingCampaignActivity.class);

				context.startActivity(intent);
			}
		});

		TextView exit = new TextView(context);
		exit.setGravity(Gravity.CENTER);
		exit.setTextColor(getResources().getColor(R.color.white));
		exit.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lock_power_off, 0, 0);
		exit.setText(R.string.exit_app);
		exit.setSingleLine();
		exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((MenuExitable) context).exit(null);
			}
		});

		TextView callServiceButton = new TextView(context);
		callServiceButton.setGravity(Gravity.CENTER);
		callServiceButton.setTextColor(getResources().getColor(R.color.white));
		callServiceButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_audio_phone, 0, 0);
		callServiceButton.setText(R.string.customer_service);
		callServiceButton.setSingleLine();
		callServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context).setIcon(R.drawable.call_contact).setTitle(R.string.customer_service)
						.setMessage(context.getString(R.string.call_service_message))
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								new PhoneUtil(context).call(context.getString(R.string.service_phone));
							}
						}).setNegativeButton(R.string.no, null).show();
			}
		});

		TextView faq = new TextView(context);
		faq.setGravity(Gravity.CENTER);
		faq.setTextColor(getResources().getColor(R.color.white));
		faq.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.faq, 0, 0);
		faq.setText(context.getString(R.string.f_and_q));
		faq.setSingleLine();
		faq.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, WebViewActivity.class);
				intent.putExtra("url", context.getString(R.string.domain) + context.getString(R.string.get_f_and_q_url));
				intent.putExtra("title", context.getString(R.string.f_and_q));

				context.startActivity(intent);
			}
		});

		itemViewList.add(account);
		itemViewList.add(myOrder);
		// itemViewList.add(myPoints);
		itemViewList.add(myCoupons);
		itemViewList.add(marketingCampaign);
		itemViewList.add(callServiceButton);
		itemViewList.add(faq);
		itemViewList.add(exit);

		setAdapter(new ListAdapter(itemViewList));
	}
}