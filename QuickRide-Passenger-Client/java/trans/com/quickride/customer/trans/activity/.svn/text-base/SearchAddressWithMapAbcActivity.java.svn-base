package com.quickride.customer.trans.activity;

import java.util.ArrayList;
import java.util.List;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.map.SerializablePoiItem;
import ac.mm.android.util.graphics.DisplayUtil;
import ac.mm.android.util.graphics.ViewUtil;
import ac.mm.android.view.QuickActionPopupWindow;
import ac.mm.android.view.ScrollPagingListView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.mapapi.core.MapAbcException;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.poisearch.PoiPagedResult;
import com.mapabc.mapapi.poisearch.PoiSearch;
import com.mapabc.mapapi.poisearch.PoiTypeDef;
import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.trans.database.dao.AddressDao;
import com.quickride.customer.trans.database.entity.Address;
import com.quickride.customer.trans.domain.RentCarStatus;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-5
 * @version 1.0
 */

public class SearchAddressWithMapAbcActivity extends MGestureSwitchPageActivity {
	private ProgressDialog progDialog;
	private AutoCompleteTextView searchbar;

	private static final int RESULT = 1000;
	private static final int RESULT_IS_NULL = 1001;
	private static final int IO_EXCEPTION = 1002;
	private static final int SEARCH_SUGGESTION = 1003;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private String commonAddressTabString = "commonAddressTab";
	private String historyAddressTabString = "historyAddressTab";

	private static String searchAddress = "";

	public static String Tag = "SearchAddressActivity";

	private ExpandApplication application;

	private DisplayUtil displayUtil;

	private AddressDao addressDao;

	private TabHost tabHost;

	private ScrollPagingListView commonAddressListView;
	private ScrollPagingListView historyAddressListView;

	private volatile QuickActionPopupWindow quickActionPopupWindow;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}

			switch (msg.what) {
			case RESULT:
				final PoiPagedResult result = (PoiPagedResult) msg.obj;

				final ScrollPagingListView poiItemListView = new ScrollPagingListView(
						SearchAddressWithMapAbcActivity.this, null);
				poiItemListView.setCacheColorHint(Color.TRANSPARENT);
				poiItemListView.setBackgroundColor(Color.WHITE);

				poiItemListView.setAdapter(createSearchAddressAdapter(result, poiItemListView));

				int title = R.string.chose_start_place;
				if (RentCarStatus.set_end_point_process == getIntent().getSerializableExtra("rentCarStatus")) {
					title = R.string.chose_end_place;
				}

				if (!isFinishing()) {
					final AlertDialog searchResultDialog = new AlertDialog.Builder(SearchAddressWithMapAbcActivity.this)
							.setTitle(title).setIcon(R.drawable.ic_menu_more).setView(poiItemListView).show();

					poiItemListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View view, int itemId, long position) {
							poiItemListView.setEnabled(false);

							searchResultDialog.dismiss();

							PoiItem poiItem = (PoiItem) view.getTag();

							returnRentCarActivity(poiItem.getTitle(), poiItem.getSnippet(), poiItem.getPoint()
									.getLatitudeE6(), poiItem.getPoint().getLongitudeE6());
						}
					});
				}

				break;

			case RESULT_IS_NULL:
				Toast.makeText(getApplicationContext(), getString(R.string.not_find_place), Toast.LENGTH_SHORT).show();

				break;

			case IO_EXCEPTION:
				Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();

				application.getNetworkUtil().handleConnectException();

				break;

			case SEARCH_SUGGESTION:
				final List<String> searchSuggestions = (List<String>) msg.obj;

				Log.d(Tag, "searchSuggestions=" + searchSuggestions);

				new AlertDialog.Builder(SearchAddressWithMapAbcActivity.this)
						.setTitle("您是不是要找：")
						.setIcon(R.drawable.ic_menu_more)
						.setItems(searchSuggestions.toArray(new String[searchSuggestions.size()]),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										searchbar.setText(searchSuggestions.get(which));
										setSearchbarSelection();
									}
								}).show();

				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_address_mapabc);

		RentCarStatus rentCarStatus = (RentCarStatus) getIntent().getSerializableExtra("rentCarStatus");
		if (RentCarStatus.set_start_point_process == rentCarStatus) {
			setTitle(getString(R.string.chose_or_search_start_place) + "：" + getIntent().getStringExtra("city"));
		} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
			setTitle(getString(R.string.chose_or_search_end_place) + "：" + getIntent().getStringExtra("city"));
		}

		application = (ExpandApplication) getApplication();

		progDialog = new ProgressDialog(this);
		progDialog.setMessage(getString(R.string.searching));

		searchbar = (AutoCompleteTextView) findViewById(R.id.searchbar);
		searchbar.setText(searchAddress);

		setSearchbarSelection();

		setupVoiceButton();

		setupClearButton();

		setupSearchButton();

		addressDao = new AddressDao(this);

		displayUtil = new DisplayUtil(this);

		setupTabHost();
	}

	private void setupVoiceButton() {
		ImageButton voiceButton = (ImageButton) findViewById(R.id.voice_button);
		voiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// 通过Intent传递语音识别的模式,开启语音
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					// 语言模式和自由形式的语音识别
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

					RentCarStatus rentCarStatus = (RentCarStatus) getIntent().getSerializableExtra("rentCarStatus");
					if (RentCarStatus.set_start_point_process == rentCarStatus) {
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.from_prompt));
					} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.to_prompt));
					}

					// 开始执行我们的Intent、语音识别
					startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					// 找不到语音设备装置
					Toast.makeText(SearchAddressWithMapAbcActivity.this,
							getString(R.string.install_google_voice_prompt), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 判断是否是我们执行的语音识别
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// 取得语音的字符
			final ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			// 设置视图更新
			final ListView mList = new ListView(this);
			mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
			mList.setCacheColorHint(Color.TRANSPARENT);

			if (!isFinishing()) {
				final AlertDialog searchResultDialog = new AlertDialog.Builder(SearchAddressWithMapAbcActivity.this)
						.setIcon(R.drawable.btn_voicesearch_hover).setTitle(R.string.voice_chose_prompt).setView(mList)
						.show();

				mList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int itemId, long position) {
						searchResultDialog.dismiss();

						searchbar.setText(results.get(itemId));

						setSearchbarSelection();

						searchAddress();
					}
				});
			}
		}
	}

	private void setSearchbarSelection() {
		Editable editable = searchbar.getText();

		Selection.setSelection(editable, editable.length());
	}

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(R.id.address_tabhost);
		tabHost.setup();

		TabSpec commonAddressTab = tabHost.newTabSpec(commonAddressTabString)
				.setIndicator(getString(R.string.common_address)).setContent(R.id.common_address);
		tabHost.addTab(commonAddressTab);

		// LinearLayout historyAddressLinearLayout = (LinearLayout)
		// findViewById(R.id.history_address);
		// historyAddressLinearLayout.startAnimation(AnimationUtils.makeInAnimation(this,
		// true));

		TabSpec historyAddressTab = tabHost.newTabSpec(historyAddressTabString)
				.setIndicator(getString(R.string.history_address)).setContent(R.id.history_address);
		tabHost.addTab(historyAddressTab);

		ViewUtil.textCentered(tabHost.getTabWidget());

		commonAddressListView = (ScrollPagingListView) findViewById(R.id.common_address_list);

		historyAddressListView = (ScrollPagingListView) findViewById(R.id.history_address_list);

		setAdapterForCommonAddressListView(commonAddressTabString);

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				refreshAddressListView(tabId);
			}
		});
	}

	private void refreshAddressListView(String tabId) {
		if (commonAddressTabString.equals(tabId)) {
			setAdapterForCommonAddressListView(tabId);
		} else if (historyAddressTabString.equals(tabId)) {
			historyAddressListView.setAdapter(createAddressListViewAdapter(historyAddressListView, tabId));
		}
	}

	private void setAdapterForCommonAddressListView(String tabId) {
		commonAddressListView.setAdapter(createAddressListViewAdapter(commonAddressListView, tabId));
	}

	private ScrollPagingListView.Adapter<Address> createAddressListViewAdapter(
			final ScrollPagingListView addressListView, final String tabId) {
		final ScrollPagingListView.Adapter<Address> adapter = new ScrollPagingListView.Adapter<Address>() {
			@Override
			protected List<Address> getNextPageItemDataList(int arg0, int nextPageCount) {
				List<Address> addressList = null;
				if (commonAddressTabString.equals(tabId)) {
					addressList = addressDao.queryCommonAddressByPageWithCity(getIntent().getStringExtra("city"),
							nextPageCount, 10);
				} else if (historyAddressTabString.equals(tabId)) {
					addressList = addressDao.queryHistoryAddressByPageWithCity(getIntent().getStringExtra("city"),
							nextPageCount, 10);
				}

				if (null == addressList || addressList.isEmpty()) {
					addressListView.notifyIsLastPage();
				}

				return addressList;
			}

			@Override
			protected View getView(int i, final Address address, View arg1, ViewGroup arg2) {
				final TextView addressTextView = new TextView(SearchAddressWithMapAbcActivity.this);
				addressTextView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT,
						ListView.LayoutParams.WRAP_CONTENT));

				if (Address.YES_TYPE == address.getCommonAddressType() && historyAddressTabString.equals(tabId)) {
					addressTextView.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(R.drawable.btn_rating_star_on_selected), null, null, null);
				} else if (Address.NO_TYPE == address.getCommonAddressType() && historyAddressTabString.equals(tabId)) {
					addressTextView.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(R.drawable.btn_rating_star_off_normal), null, null, null);
				}

				addressTextView.setText(address.getTitle());
				addressTextView.setTextColor(Color.BLACK);
				addressTextView.setTextSize(16);
				addressTextView.setTag(address);
				addressTextView.setGravity(Gravity.CENTER_VERTICAL);

				if (i == 0 && getCount() > 1) {
					addressTextView.setBackgroundResource(R.drawable.arrow_item_up);
				} else if (addressListView.isLastPage() && i == getCount() - 1 && i > 0) {
					addressTextView.setBackgroundResource(R.drawable.arrow_item_down);
				} else {
					addressTextView.setBackgroundResource(R.drawable.arrow_item);
				}

				addressTextView.setPadding(displayUtil.dip2px(15), 0, displayUtil.dip2px(15), 0);

				addressTextView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View view) {
						final ViewGroup quickActionView = (ViewGroup) getLayoutInflater().inflate(
								R.layout.address_quickaction, null);

						final QuickActionPopupWindow quickActionPopupWindow = new QuickActionPopupWindow(
								SearchAddressWithMapAbcActivity.this, quickActionView, view, quickActionView
										.findViewById(R.id.quickaction_target), LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT) {
							@Override
							public void onAnchorClick(View anchor) {
								setGlobalQuickActionPopupWindow(this);
							}

							@Override
							protected void show(View anchor) {
								showAsDropDown(anchor, 0, -6);
							}
						};
						quickActionPopupWindow.show();

						setGlobalQuickActionPopupWindow(quickActionPopupWindow);

						if (Address.YES_TYPE == address.getCommonAddressType()) {
							quickActionView.findViewById(R.id.set_common_address).setVisibility(View.GONE);
						} else {
							quickActionView.findViewById(R.id.set_common_address).setOnClickListener(
									new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											address.setCommonAddressType(Address.YES_TYPE);

											addressDao.update(address);

											quickActionPopupWindow.dismiss();

											addressTextView.setCompoundDrawablesWithIntrinsicBounds(getResources()
													.getDrawable(R.drawable.btn_rating_star_on_selected), null, null,
													null);
											quickActionView.findViewById(R.id.set_common_address).setVisibility(
													View.GONE);
										}
									});
						}

						setupDeleteAddressButton(tabId, address, quickActionView, quickActionPopupWindow);

						ImageButton useAddressButton = (ImageButton) quickActionView.findViewById(R.id.use_address);

						RentCarStatus rentCarStatus = (RentCarStatus) getIntent().getSerializableExtra("rentCarStatus");
						if (RentCarStatus.set_start_point_process == rentCarStatus) {
							useAddressButton.setImageResource(R.drawable.start_point);
						} else if (RentCarStatus.set_end_point_process == rentCarStatus) {
							useAddressButton.setImageResource(R.drawable.end_point);
						}

						useAddressButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								returnRentCarActivity(address.getTitle(), "", address.getLatitudeE6(),
										address.getLongitudeE6());

								quickActionPopupWindow.dismiss();
							}
						});
					}
				});

				return addressTextView;
			}

			@Override
			protected void onPageChanged(List<Address> arg0) {
				// TODO Auto-generated method stub
			}
		};

		return adapter;
	}

	private synchronized void setGlobalQuickActionPopupWindow(final QuickActionPopupWindow quickActionPopupWindow) {
		if (null != this.quickActionPopupWindow) {
			this.quickActionPopupWindow.dismiss();
		}

		this.quickActionPopupWindow = quickActionPopupWindow;
	}

	private void setupDeleteAddressButton(final String tabId, final Address address, final ViewGroup quickActionView,
			final QuickActionPopupWindow quickActionPopupWindow) {
		if (Address.YES_TYPE == address.getUndeletableType() && commonAddressTabString.equals(tabId)) {
			quickActionView.findViewById(R.id.delete_address).setVisibility(View.GONE);
		} else if (Address.NO_TYPE == address.getUndeletableType() || historyAddressTabString.equals(tabId)) {
			quickActionView.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					new AlertDialog.Builder(SearchAddressWithMapAbcActivity.this).setIcon(R.drawable.ic_menu_delete)
							.setTitle(R.string.delete_address).setMessage(R.string.confirm_delete)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									if (commonAddressTabString.equals(tabId)) {
										address.setCommonAddressType(Address.NO_TYPE);

										addressDao.update(address);
									} else if (historyAddressTabString.equals(tabId)) {
										address.setHistoryAddressType(Address.NO_TYPE);

										addressDao.update(address);
									}

									if (Address.NO_TYPE == address.getCommonAddressType()
											&& Address.NO_TYPE == address.getHistoryAddressType()) {
										addressDao.deleteById(address.get_id());
									}

									quickActionPopupWindow.dismiss();

									refreshAddressListView(tabId);
								}
							}).setNegativeButton(R.string.no, null).show();
				}
			});
		}
	}

	private ScrollPagingListView.Adapter<PoiItem> createSearchAddressAdapter(final PoiPagedResult result,
			final ScrollPagingListView poiItemListView) {
		ScrollPagingListView.Adapter<PoiItem> adapter = new ScrollPagingListView.Adapter<PoiItem>() {
			@Override
			protected List<PoiItem> getNextPageItemDataList(int lastListItemIndex, int nextPageCount) {
				return getPoiItemList(poiItemListView, result, nextPageCount);
			}

			@Override
			protected void onPageChanged(List<PoiItem> itemViewList) {
			}

			@Override
			protected View getView(int i, PoiItem data, View convertView, ViewGroup viewgroup) {
				LinearLayout linearLayout = new LinearLayout(SearchAddressWithMapAbcActivity.this);
				linearLayout.setMinimumHeight(60);
				linearLayout.setTag(data);
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				linearLayout.setGravity(Gravity.CENTER_VERTICAL);
				linearLayout.setPadding(12, 0, 12, 0);

				TextView titleTextView = new TextView(SearchAddressWithMapAbcActivity.this);
				titleTextView.setText(data.getTitle());
				titleTextView.setTextSize(20);
				titleTextView.setTextColor(Color.BLACK);

				TextView addressTextView = new TextView(SearchAddressWithMapAbcActivity.this);
				addressTextView.setText(null == data.getSnippet() ? getIntent().getStringExtra("city") : data
						.getSnippet());
				addressTextView.setTextSize(14);
				addressTextView.setTextColor(Color.BLACK);

				linearLayout.addView(titleTextView);
				linearLayout.addView(addressTextView);

				return linearLayout;
			}
		};

		// adapter.setNextPageCount(1);

		return adapter;
	}

	private void setupSearchButton() {
		ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchAddress();
			}
		});
	}

	private void searchAddress() {
		final String query = searchbar.getText().toString().trim();

		if (null == query || "".equals(query)) {
			Toast.makeText(SearchAddressWithMapAbcActivity.this, R.string.input_search_address, Toast.LENGTH_SHORT)
					.show();

			searchbar.setError(getString(R.string.input_search_address));

			return;
		}

		searchAddress = query;

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					PoiPagedResult result = createPoiSearch(query).searchPOI();

					if (!progDialog.isShowing()) {
						return;
					}

					if (result == null) {
						handler.sendMessage(Message.obtain(handler, RESULT_IS_NULL));

						return;
					}

					int pageCount = result.getPageCount();

					List<PoiItem> poiItemList;
					if (pageCount < 1 || (poiItemList = result.getPage(1)) == null || poiItemList.isEmpty()) {
						handleNoSearchResult(result);

						return;
					}

					handler.sendMessage(Message.obtain(handler, RESULT, result));
				} catch (MapAbcException e) {
					e.printStackTrace();

					handler.sendMessage(Message.obtain(handler, IO_EXCEPTION));
				} finally {
					progDialog.dismiss();
				}
			}
		});

		progDialog.show();
		t.start();
	}

	private void handleNoSearchResult(PoiPagedResult result) {
		List<String> searchSuggestions = result.getSearchSuggestions();
		if (null != searchSuggestions && !searchSuggestions.isEmpty()) {
			handler.sendMessage(Message.obtain(handler, SEARCH_SUGGESTION, searchSuggestions));
		} else {
			handler.sendMessage(Message.obtain(handler, RESULT_IS_NULL));
		}
	}

	private void setupClearButton() {
		Button clearButton = (Button) findViewById(R.id.clear_content);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchbar.setText("");
			}
		});
	}

	private PoiSearch createPoiSearch(final String query) {
		PoiSearch poiSearch;
		String city = getIntent().getStringExtra("city");
		if (getResources().getStringArray(R.array.city_name)[0].equals(city)) {
			poiSearch = new PoiSearch(SearchAddressWithMapAbcActivity.this, getString(R.string.mapabc_key),
					new PoiSearch.Query(query, PoiTypeDef.All, "021"));
		} else if (getResources().getStringArray(R.array.city_name)[1].equals(city)) {
			poiSearch = new PoiSearch(SearchAddressWithMapAbcActivity.this, getString(R.string.mapabc_key),
					new PoiSearch.Query(query, PoiTypeDef.All, "028"));
		} else {
			poiSearch = new PoiSearch(SearchAddressWithMapAbcActivity.this, getString(R.string.mapabc_key),
					new PoiSearch.Query(query, PoiTypeDef.All));
		}
		return poiSearch;
	}

	private List<PoiItem> getPoiItemList(ScrollPagingListView poiItemListView, PoiPagedResult result, int nextPageCount) {
		try {
			int pageCount = result.getPageCount();

			if (nextPageCount <= pageCount) {
				List<PoiItem> poiItemList = result.getPage(nextPageCount);

				Log.d(Tag, "pageCount=" + pageCount + ", nextPageCount=" + nextPageCount + ", poiItemList="
						+ poiItemList);

				return poiItemList;
			} else {
				poiItemListView.notifyIsLastPage();
			}
		} catch (MapAbcException e) {
			e.printStackTrace();
			handler.sendMessage(Message.obtain(handler, IO_EXCEPTION));
		}

		return null;
	}

	private void returnRentCarActivity(String title, String snippet, int latitudeE6, int longitudeE6) {
		Intent intent = new Intent(application, RentCarWithMapAbcActivity.class);
		intent.putExtra("SerializablePoiItem", new SerializablePoiItem("", latitudeE6, longitudeE6, title
				+ (null == snippet || "".equals(snippet) ? "" : " " + snippet), getIntent().getStringExtra("city")));

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	protected void finishActivityAnimation() {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != addressDao) {
			addressDao.close();
		}

		if (null != quickActionPopupWindow) {
			quickActionPopupWindow.dismiss();
		}

		if (null != progDialog) {
			progDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (null != quickActionPopupWindow && quickActionPopupWindow.isShowing()) {
			quickActionPopupWindow.dismiss();
		} else {
			finish();
		}
	}
}
