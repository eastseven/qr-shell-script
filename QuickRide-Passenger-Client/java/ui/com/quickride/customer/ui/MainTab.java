package com.quickride.customer.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.util.DebugUtil;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.ui.activity.AccountActivity;
import com.quickride.customer.ui.activity.FaqActivity;
import com.quickride.customer.ui.activity.MyCouponsActivity;
import com.quickride.customer.ui.activity.MyOrderListActivity;
import com.quickride.customer.ui.activity.OrderProductListActivity;
import com.quickride.customer.ui.adapter.Airport;

/**
 * 首页
 * @author eastseven
 *
 */
public class MainTab extends TabActivity implements OnTabChangeListener, OnClickListener {

	private static final String tag = "QR_MainTabHost";
	
	final String TAB_AIRPLANE = "main_content_airplane_layout";
	final String TAB_CAR      = "main_content_car_layout";
	final String TAB_COUPON   = "main_content_coupon_layout";
	final String TAB_MORE     = "main_content_more_layout";
	
	//Header Title
	TextView title;
	//Button leftButton, rightButton;
	
	//TabHost 标签
	TabHost tabHost;

	//Tab Airplane Layout Elements
	//接机送机页面相关元素
	RadioGroup airplaneRadioGroup;
	EditText   airplaneStartAddressEditText;
	EditText   airplaneEndAddressEditText;
	EditText   airplaneFlightDateEditText;
	EditText   airplaneFlightNumberEditText;
	EditText   airplaneCodeEditText;
	Button     airplaneCheckButton;
	Button     airplaneResetButton;
	
	ArrayList<Airport> airportList = new ArrayList<Airport>(3);
	final String[] airports = new String[] {"上海浦东国际机场","上海虹桥国际机场T1航站楼","上海虹桥国际机场2号航站楼"};
	
	//Tab Car Layout Elements
	//查询预定
	EditText   carStartAddressEditText;
	EditText   carEndAddressEditText;
	EditText   carPickupTimeEditText;
	EditText   carCodeEditText;
	Button     carCheckButton;
	Button     carResetButton;
	
	//Tab Coupon Layout Elements
	//特价优惠
	RadioGroup couponAirportRadioGroup;
	RadioGroup couponRadioGroup;
	Spinner    couponCarGradeSpinner;
	EditText   couponPickupTimeEditText;
	Button     couponCheckButton;
	Button     couponResetButton;
	
	//Tab More Layout Elements
	//更多
	Button moreLogoutButton, moreMyOrderButton, moreMyCouponButton, moreMyAccountButton, moreFaqButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		
		this.initAirportList();
		
		this.initHeadBar();
		this.initTabHost();
		
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(tag+".onTabChanged", "tabId="+tabId);
		
		if(tabId.equalsIgnoreCase(TAB_AIRPLANE)) {
			this.title.setText(getString(R.string.main_airplane));
			this.initTabAirplane(this.tabHost.getCurrentTabView());
			
		} else if (tabId.equalsIgnoreCase(TAB_CAR)) {
			this.title.setText(getString(R.string.main_car));
			this.initTabCar(this.tabHost.getCurrentTabView());
			
		} else if (tabId.equalsIgnoreCase(TAB_COUPON)) {
			this.title.setText(getString(R.string.main_coupon));
			this.initTabCoupon(this.tabHost.getCurrentTabView());
			
		} else if (tabId.equalsIgnoreCase(TAB_MORE)) {
			this.title.setText(getString(R.string.main_more));
			this.initTabMore(this.tabHost.getCurrentTabView());
			
		}
	}
	
	void initAirportList() {
		this.airportList.add(new Airport(airports[0], 121.8023,   31.1501));
		this.airportList.add(new Airport(airports[1], 121.348033, 31.193985));
		this.airportList.add(new Airport(airports[2], 121.326395, 31.195208));
	}
	
	void initHeadBar() {
		this.title = (TextView) findViewById(R.id.main_header_title);
		this.title.setText(getString(R.string.main_airplane));
		
	}
	
	void initTabHost() {
		this.tabHost = getTabHost();
		this.tabHost.addTab(tabHost.newTabSpec(TAB_AIRPLANE).setIndicator(getString(R.string.main_airplane)).setContent(R.id.main_content_airplane_layout));
		this.tabHost.addTab(tabHost.newTabSpec(TAB_CAR).setIndicator(getString(R.string.main_car)).setContent(R.id.main_content_car_layout));
		this.tabHost.addTab(tabHost.newTabSpec(TAB_COUPON).setIndicator(getString(R.string.main_coupon)).setContent(R.id.main_content_coupon_layout));
		this.tabHost.addTab(tabHost.newTabSpec(TAB_MORE).setIndicator(getString(R.string.main_more)).setContent(R.id.main_content_more_layout));
		
		this.tabHost.setOnTabChangedListener(this);
		//set default tab view airplane layout
		this.initTabAirplane(this.tabHost.getCurrentTabView());
	}
	
	void initTabAirplane(View currentTab) {
		Log.d(tag, "initTabAirplane=" + currentTab);
		//init form elements
		this.airplaneRadioGroup           = (RadioGroup) findViewById(R.id.main_content_airplane_radio_group);
		this.airplaneStartAddressEditText = (EditText)   findViewById(R.id.main_content_airplane_start_address);
		this.airplaneEndAddressEditText   = (EditText)   findViewById(R.id.main_content_airplane_end_address);
		this.airplaneFlightDateEditText   = (EditText)   findViewById(R.id.main_content_airplane_flight_date);
		this.airplaneFlightNumberEditText = (EditText)   findViewById(R.id.main_content_airplane_flight_no);
		this.airplaneCodeEditText         = (EditText)   findViewById(R.id.main_content_airplane_code);
		this.airplaneCheckButton          = (Button)     findViewById(R.id.main_content_airplane_check);
		this.airplaneResetButton          = (Button)     findViewById(R.id.main_content_airplane_reset);

		this.setupAirplaneRadioGroup();
		this.setupAirplaneFlightDateEditText();
		this.setupAirplaneCheckButton();
		this.setupAirplaneResetButton();
		
	}
	
	void setupAirplaneRadioGroup() {
		//接机送机
		this.airplaneRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			Airport airport;
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d(tag, "group=" + group + ", checkedId=" + checkedId);
				
				final int _checkedId = checkedId;
				
				AlertDialog.Builder b = new AlertDialog.Builder(group.getContext());
				b.setTitle(R.string.main_select_airport);
				b.setSingleChoiceItems(airports, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(tag, "dialog="+dialog+", which="+which+", checkedId="+_checkedId);
						airport = airportList.get(which);
						dialog.dismiss();
						
						switch (_checkedId) {
						case R.id.main_content_airplane_radio_pickup:
							airplaneStartAddressEditText.requestFocus();
							airplaneStartAddressEditText.setText("");
							airplaneEndAddressEditText.setText(airport.getName());
							break;
						case R.id.main_content_airplane_radio_seeoff:
							airplaneEndAddressEditText.requestFocus();
							airplaneStartAddressEditText.setText(airport.getName());
							airplaneEndAddressEditText.setText("");
							break;
						default:
							break;
						}
					}
				});
				b.setPositiveButton(R.string.cancel, null);
				b.show();
				
			}
		});
	}
	
	void setupAirplaneFlightDateEditText() {
		//set datepicker
		this.airplaneFlightDateEditText.setKeyListener(null);
		this.airplaneFlightDateEditText.setInputType(InputType.TYPE_NULL);
		this.airplaneFlightDateEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "航班日期选择器");
				Calendar dateTime  = Calendar.getInstance();
				int year           = dateTime.get(Calendar.YEAR);
				int monthOfYear    = dateTime.get(Calendar.MONTH);
				int dayOfMonth     = dateTime.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog datePickerDialog = new DatePickerDialog(MainTab.this, new DatePickerDialog.OnDateSetListener() {
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
	}
	
	//接机送机-查询按钮
	void setupAirplaneCheckButton() {
		//set event handle
		this.airplaneCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "form submit");
				int checkedId = airplaneRadioGroup.getCheckedRadioButtonId();
				switch (checkedId) {
				case R.id.main_content_airplane_radio_pickup:
					Toast.makeText(v.getContext(), getString(R.string.main_pickup), Toast.LENGTH_LONG).show();
					break;
				case R.id.main_content_airplane_radio_seeoff:
					Toast.makeText(v.getContext(), getString(R.string.main_seeoff), Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		});
	}
	
	//接机送机-重置按钮
	void setupAirplaneResetButton() {
		//set event handle
		this.airplaneResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				airplaneRadioGroup.clearCheck();
				airplaneFlightNumberEditText.setText(null);
				airplaneFlightDateEditText.setText(null);
				airplaneStartAddressEditText.setText(null);
				airplaneEndAddressEditText.setText(null);
				airplaneCodeEditText.setText(null);
			}
		});
	}
	
	void initTabCar(View currentTab) {
		Log.d(tag, "initTabCar=" + currentTab);
		
		this.carStartAddressEditText = (EditText) findViewById(R.id.main_content_car_start_address);
		this.carEndAddressEditText   = (EditText) findViewById(R.id.main_content_car_end_address);
		this.carPickupTimeEditText   = (EditText) findViewById(R.id.main_content_car_pickup_time);
		this.carCodeEditText         = (EditText) findViewById(R.id.main_content_car_code);
		this.carCheckButton          = (Button)   findViewById(R.id.main_content_car_check);
		this.carResetButton          = (Button)   findViewById(R.id.main_content_car_reset);
		
		this.setupCarPickupTimeEditText();
		this.setupCarCheckButton();
		this.setupCarResetButton();
		
	}
	
	//设置日期选择器
	void setupCarPickupTimeEditText() {
		this.carPickupTimeEditText.setKeyListener(null);
		this.carPickupTimeEditText.setInputType(InputType.TYPE_NULL);
		this.carPickupTimeEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar dateTime  = Calendar.getInstance();
				int year           = dateTime.get(Calendar.YEAR);
				int monthOfYear    = dateTime.get(Calendar.MONTH);
				int dayOfMonth     = dateTime.get(Calendar.DAY_OF_MONTH);
				
				DatePickerDialog datePickerDialog = new DatePickerDialog(MainTab.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Log.d(tag, "year=" + year + ",month=" + (monthOfYear + 1) + ",day=" + dayOfMonth);
						Calendar currentTime = Calendar.getInstance();
						int hour = currentTime.get(Calendar.HOUR_OF_DAY) + 2;
						int minute = currentTime.get(Calendar.MINUTE);
						String text = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日 " + hour + ":" + minute;
						carPickupTimeEditText.setText(text);
					}
				}, year, monthOfYear, dayOfMonth);
				datePickerDialog.show();
			}
		});
	}
	
	//查询预定-查询按钮
	void setupCarCheckButton() {
		this.carCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	//查询预定-重置按钮
	void setupCarResetButton() {
		this.carResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				carStartAddressEditText.setText(null);
				carEndAddressEditText.setText(null);
				carPickupTimeEditText.setText(null);
				carCodeEditText.setText(null);
			}
		});
	}
	
	void initTabCoupon(View currentTab) {
		Log.d(tag, "initTabCoupon=" + currentTab);
		
		this.couponAirportRadioGroup  = (RadioGroup) findViewById(R.id.main_content_coupon_radio_group_airport);
		this.couponRadioGroup         = (RadioGroup) findViewById(R.id.main_content_coupon_radio_group);
		this.couponCarGradeSpinner    = (Spinner)    findViewById(R.id.main_content_coupon_car_grade);
		this.couponPickupTimeEditText = (EditText)   findViewById(R.id.main_content_coupon_pickup_time); 
		this.couponCheckButton        = (Button)     findViewById(R.id.main_content_coupon_check);
		this.couponResetButton        = (Button)     findViewById(R.id.main_content_coupon_reset);
		
		this.setupCouponCarGradeSpinner();
		this.setupCouponPickupTimeEditText();
		this.setupCouponCheckButton();
		this.setupCouponResetButton();
	}
	
	void setupCouponCarGradeSpinner() {
		this.couponCarGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d(tag, "parent="+parent+",view="+view+",position="+position+",id="+id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d(tag, "onNothingSelected() parent="+parent);
			}
		});
		
		EndpointClient request = new EndpointClient(MainTab.this) {
			
			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				return getCarGrades(null);
			}
			
			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				DebugUtil.print(result, MainTab.class);
				if(result.get(StatusCode.FIELD_SUCCESS).equals(Boolean.FALSE)) {
					Toast.makeText(MainTab.this, result.get(StatusCode.FIELD_MESSAGE).toString(), Toast.LENGTH_LONG).show();
					return;
				}
				
				Object object = result.get(StatusCode.FIELD_RESULT);
				if(object instanceof ArrayList<?>) {
					ArrayList<?> data = (ArrayList<?>) object;
					int len = data.size();
					String[] objects = new String[len+1];
					objects[0] = "不限";
					for(int index = 0; index < len; index++) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> carGrade = (HashMap<String, Object>) data.get(index);
						objects[index+1] = carGrade.get("name").toString();
						Log.d(tag, "" + carGrade.get("name"));
					}
					
					SpinnerAdapter adapter = new ArrayAdapter<String>(MainTab.this, android.R.layout.simple_spinner_item, objects);
					couponCarGradeSpinner.setAdapter(adapter);
				}
			}
		};
		request.execute();
	}
	
	void setupCouponPickupTimeEditText() {
		this.couponPickupTimeEditText.setKeyListener(null);
		this.couponPickupTimeEditText.setInputType(InputType.TYPE_NULL);
		this.couponPickupTimeEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar dateTime  = Calendar.getInstance();
				int year           = dateTime.get(Calendar.YEAR);
				int monthOfYear    = dateTime.get(Calendar.MONTH);
				int dayOfMonth     = dateTime.get(Calendar.DAY_OF_MONTH);
				
				DatePickerDialog datePickerDialog = new DatePickerDialog(MainTab.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Log.d(tag, "year=" + year + ",month=" + (monthOfYear + 1) + ",day=" + dayOfMonth);
						String text = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日 ";
						final String dateText = text;
						Calendar currentTime = Calendar.getInstance();
						int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY) + 2;
						int minute = currentTime.get(Calendar.MINUTE);
						
						TimePickerDialog timePickerDialog = new TimePickerDialog(MainTab.this, new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								String text = dateText + " " + hourOfDay + ":" + minute;
								couponPickupTimeEditText.setText(text);
							}
						}, hourOfDay, minute, true);
						timePickerDialog.show();
						
					}
				}, year, monthOfYear, dayOfMonth);
				datePickerDialog.show();
			}
		});
	}
	
	//特价优惠-查询按钮
	void setupCouponCheckButton() {
		this.couponCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//检查提交数据
				int checkedId = couponAirportRadioGroup.getCheckedRadioButtonId();
				if(checkedId == -1) {
					Toast.makeText(MainTab.this, "请选择机场", Toast.LENGTH_LONG).show();
					return;
				}
				
				checkedId = couponRadioGroup.getCheckedRadioButtonId();
				if(checkedId == -1) {
					Toast.makeText(MainTab.this, "请选择接机或者送机", Toast.LENGTH_LONG).show();
					return;
				}
				Log.d(tag, "检查：" + couponPickupTimeEditText.getText().toString().equals(""));
				if("".equals(couponPickupTimeEditText.getText().toString())) {
					Toast.makeText(MainTab.this, "请选择时间", Toast.LENGTH_LONG).show();
					return;
				}
				
				String address = "";
				switch (couponAirportRadioGroup.getCheckedRadioButtonId()) {
				case R.id.main_content_coupon_airport0:
					address += airports[0];
					break;
				case R.id.main_content_coupon_airport1:
					address += airports[1];
					break;
				case R.id.main_content_coupon_airport2:
					address += airports[2];
					break;
				default:
					break;
				}
				
				//跳转到特价产品列表页面
				Button btn = (Button) findViewById(couponRadioGroup.getCheckedRadioButtonId());
				final String type = btn.getText().toString();
				final String carGrade = (couponCarGradeSpinner.getSelectedItem().toString().equalsIgnoreCase("不限") ? "" : couponCarGradeSpinner.getSelectedItem().toString());
				final String pickupTime = couponPickupTimeEditText.getText().toString().replace("年", "-").replace("月", "-").replace("日", "").trim() + ":00";
				final String _address = address;
				
				Intent intent = new Intent(MainTab.this, OrderProductListActivity.class);
				intent.putExtra("search_LEC_pickupTime", pickupTime);
				if(!"".equals(carGrade)) intent.putExtra("search_LIKES_carGrade", carGrade);
				
				if(type.equalsIgnoreCase(getString(R.string.main_pickup))) {
					intent.putExtra("search_LIKES_pickupAddress", _address);
				} else {
					intent.putExtra("search_LIKES_dischargeAddress", _address);
				}
				intent.putExtra("page_pageNo", "1");
				intent.putExtra("page_pageSize", "10");
				
				startActivity(intent);
			}
		});
	}
	
	//特价优惠-重置按钮
	void setupCouponResetButton() {
		this.couponResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				couponAirportRadioGroup.clearCheck();
				couponRadioGroup.clearCheck();
				couponCarGradeSpinner.setSelection(0);
				couponPickupTimeEditText.setText(null);
			}
		});
	}
	
	//更多页面
	void initTabMore(View currentTab) {
		Log.d(tag, "initTabMore=" + currentTab);
		
		this.moreLogoutButton    = (Button) findViewById(R.id.main_more_logout);
		this.moreMyOrderButton   = (Button) findViewById(R.id.main_more_my_order);
		this.moreMyCouponButton  = (Button) findViewById(R.id.main_more_my_coupon);
		this.moreMyAccountButton = (Button) findViewById(R.id.main_more_my_account);
		this.moreFaqButton       = (Button) findViewById(R.id.main_more_faq);
		
		this.moreLogoutButton.setOnClickListener(this);
		this.moreMyOrderButton.setOnClickListener(this);
		this.moreMyCouponButton.setOnClickListener(this);
		this.moreMyAccountButton.setOnClickListener(this);
		this.moreFaqButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_more_logout:
			Toast.makeText(v.getContext(), R.string.main_more_logout, Toast.LENGTH_LONG).show();
			finish();
			break;
		case R.id.main_more_my_order:
			startActivity(new Intent(this, MyOrderListActivity.class));
			break;
		case R.id.main_more_my_coupon:
			startActivity(new Intent(this, MyCouponsActivity.class));
			break;
		case R.id.main_more_my_account:
			startActivity(new Intent(this, AccountActivity.class));
			break;
		case R.id.main_more_faq:
			startActivity(new Intent(this, FaqActivity.class));
			break;

		default:
			break;
		}
	}
}
