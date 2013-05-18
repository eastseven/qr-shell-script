package com.quickride.customer.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.quickride.customer.R;

@Deprecated
public class Main extends Activity implements View.OnClickListener, OnCheckedChangeListener {

	private static final String tag = "MainRadioTab";
	
	//header buttons
	Button homeButton;
	Button nextButton;
	TextView titleTextView;
	final String[] titles = new String[] {"接机送机", "查询预定", "特价优惠", "更多"};
	
	//content viewswitch
	ViewFlipper viewFlipper;
	
	//footer radiobuttons
	RadioButton airplaneRadioButton;
	RadioButton carRadioButton;
	RadioButton couponRadioButton;
	RadioButton moreRadioButton;
	ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>(4);
	
	//footer radiobuttons > airplane
	RadioGroup airplaneRadioGroup;
	EditText airplaneStartAddressEditText;
	EditText airplaneEndAddressEditText;
	EditText airplaneFlightDateEditText;
	EditText airplaneFlightNumberEditText;
	EditText airplaneCodeEditText;
	Button airplaneCheckButton;
	
	//footer radiobuttons > car
	Button carCheckButton;
	
	//footer radiobuttons > coupon
	Button couponCheckButton;
	
	//footer radiobuttons > more
	ListView moreListView;
	final String[] moreListMenus = new String[] {"退出当前账号", "我的订单", "我的优惠券", "邀请亲友", "账号管理", "常见问题"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.initHeadBar();
		this.initCenterContent();
		this.initFootBar();
	}
	
	private void initHeadBar() {
		Log.d(tag+".initHeadBar", "初始化Header内的按钮");
		this.homeButton    = (Button) findViewById(R.id.main_header_home);
		this.nextButton    = (Button) findViewById(R.id.main_header_next);
		this.titleTextView = (TextView) findViewById(R.id.main_header_title);
		this.titleTextView.setText(this.titles[0]);//默认显示第一页
		
		this.homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "首页");
			}
		});
		
		this.nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "下页");
			}
		});
	}
	
	private void initCenterContent() {
		Log.d(tag+".initCenterContent", "初始化CenterContent内的组件");
		//this.viewFlipper = (ViewFlipper) findViewById(R.id.main_content);
		this.viewFlipper.setDisplayedChild(0);////默认显示第一页
		
		this.initAirplaneLayout();
		this.initCarLayout();
		this.initCouponLayout();
		this.initMoreLayout();
		
	}
	
	private void initFootBar() {
		Log.d(tag+".initFootBar", "初始化Footer内的按钮");
		
//		this.airplaneRadioButton = (RadioButton) findViewById(R.id.main_airplane);
//		this.carRadioButton      = (RadioButton) findViewById(R.id.main_car);
//		this.couponRadioButton   = (RadioButton) findViewById(R.id.main_coupon);
//		this.moreRadioButton     = (RadioButton) findViewById(R.id.main_more);
		
		radioButtons.add(0, airplaneRadioButton);
		radioButtons.add(1, carRadioButton);
		radioButtons.add(2, couponRadioButton);
		radioButtons.add(3, moreRadioButton);
		for (RadioButton rb : radioButtons) {
			rb.setOnCheckedChangeListener(this);
			rb.setOnClickListener(this);
		}
		
		this.airplaneRadioButton.setChecked(Boolean.TRUE);//默认选中第一个按钮
		this.setupAirplaneLayout(this.viewFlipper.getCurrentView());
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.d(tag+".onCheckedChanged", "buttonView=" + buttonView + ", isChecked=" + isChecked + ", isContains=" + radioButtons.contains(buttonView));
		int whichChild = radioButtons.indexOf(buttonView);
		
		if(isChecked) {
			this.viewFlipper.setDisplayedChild(whichChild);
			
			for (RadioButton rb : radioButtons) {
				if(rb.equals(buttonView)) {
					this.titleTextView.setText(rb.getText());
					continue;
				}
				rb.setChecked(!isChecked);
			}
		}
		
	}

	/**
	 * 专门给RadioButton用的，其他button事件请自行实现OnClick事件
	 */
	@Override
	public void onClick(View v) {
		Log.d(tag+".onClick", "OnClickListener.view = " + v + ", isContains=" + radioButtons.contains(v));
		
		View currentView = this.viewFlipper.getCurrentView();
		int index = radioButtons.indexOf(v);
		
		this.titleTextView.setText(this.titles[index]);
		
		switch (index) {
		case 0:
			
			setupAirplaneLayout(currentView);
			break;
		case 1:
			setupCarLayout(currentView);
			break;
		case 2:
			setupCouponLayout(currentView);
			break;
		case 3:
			setupMoreLayout(currentView);
			break;
		default:
			break;
		}
		
	}
	
	//接机送机
	private void initAirplaneLayout() {
		Log.d(tag, "initAirplaneLayout");
		
		this.airplaneRadioGroup           = (RadioGroup) findViewById(R.id.main_content_airplane_radio_group);
		this.airplaneStartAddressEditText = (EditText)   findViewById(R.id.main_content_airplane_start_address);
		this.airplaneEndAddressEditText   = (EditText)   findViewById(R.id.main_content_airplane_end_address);
		this.airplaneFlightDateEditText   = (EditText)   findViewById(R.id.main_content_airplane_flight_date);
		this.airplaneFlightNumberEditText = (EditText)   findViewById(R.id.main_content_airplane_flight_no);
		this.airplaneCodeEditText         = (EditText)   findViewById(R.id.main_content_airplane_code);
		this.airplaneCheckButton          = (Button)     findViewById(R.id.main_content_airplane_check);
		
	}
	
	private void setupAirplaneLayout(View currentView) {
		Log.d(tag, "setupAirplaneLayout");
		
		this.airplaneFlightDateEditText.setKeyListener(null);
		this.airplaneFlightDateEditText.setInputType(InputType.TYPE_NULL);
		this.airplaneFlightDateEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "航班日期选择器");
				Calendar dateTime   = Calendar.getInstance();
				int year            = dateTime.get(Calendar.YEAR);
				int monthOfYear     = dateTime.get(Calendar.MONTH);
				int dayOfMonth      = dateTime.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog datePickerDialog = new DatePickerDialog(Main.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Log.d(tag, "year=" + year + ",month=" + (monthOfYear + 1) + ",day=" + dayOfMonth);
						String text = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
						airplaneFlightDateEditText.setText(text);
					}
				}, year, monthOfYear, dayOfMonth);
				datePickerDialog.show();
			}
		});
		
		this.airplaneCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "接机送机.查询");
				
				airplaneCodeEditText.getText();
			}
		});
	}
	
	//查询预定
	private void initCarLayout() {
		Log.d(tag, "initCarLayout");
		
		this.carCheckButton = (Button) findViewById(R.id.main_content_car_check);
		this.carCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "查询预定.查询");
			}
		});
	}
	
	private void setupCarLayout(View currentView) {
		Log.d(tag, "setupCarLayout");
	}
	
	//特价优惠
	private void initCouponLayout() {
		Log.d(tag, "initCouponLayout");
		
		this.couponCheckButton = (Button) findViewById(R.id.main_content_coupon_check);
		this.couponCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "特价优惠.查询");
			}
		});
	}
	
	private void setupCouponLayout(View currentView) {
		Log.d(tag, "setupCouponLayout");
	};
	
	//更多
	private void initMoreLayout() {
		Log.d(tag, "initMoreLayout");
		
		//this.moreListView = (ListView) findViewById(R.id.main_more_list);
		//this.moreListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, moreListMenus));
		
	}
	
	private void setupMoreLayout(View currentView) {
		Log.d(tag, "setupMoreLayout");
		this.moreListView.setOnItemClickListener(new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Log.d(tag+".onItemClickListener", ""+v.getClass()+",id="+id+",position="+position);
			}
			
		});
	};
}
