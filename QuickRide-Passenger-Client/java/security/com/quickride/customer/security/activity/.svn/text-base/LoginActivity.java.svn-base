package com.quickride.customer.security.activity;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.communication.PhoneUtil;
import ac.mm.android.util.download.AutoUpdater;
import ac.mm.android.util.graphics.DisplayUtil;
import ac.mm.android.util.graphics.ViewUtil;
import ac.mm.android.util.security.Validator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MBaseActivity;
import com.quickride.customer.common.util.DebugUtil;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.security.util.AutoLoginUtil;
import com.quickride.customer.security.util.ValidatorUtil;
import com.quickride.customer.trans.activity.RentCarWithMapAbcActivity;
import com.quickride.customer.ui.MainTab;

public class LoginActivity extends MBaseActivity {

	private static final String tag = "QR_LoginActivity";

	private EditText userNameTextView;
	private EditText userPasswordEditText;
	private EditText captchaEditText;
	private TextView verificationCodeTextView;
	private CheckBox aotuLoginCheckBox;

	private DisplayUtil displayUtil;
	private PhoneUtil phoneUtil;

	private Validator validator;

	private ExpandApplication application;

	public static final String SPRING_SECURITY_REMEMBER_ME_COOKIE = "SPRING_SECURITY_REMEMBER_ME_COOKIE";
	public static final String USER_NAME = "USER_NAME";
	public static final String REAL_NAME = "REAL_NAME";// realName

	private ProgressDialog progressDialog;

	private boolean isCheckedApp;

	private boolean invite = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		application = (ExpandApplication) getApplication();

		application.setExitedApp(false);
		application.setDebug(false);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		setTitle(R.string.login);

		phoneUtil = new PhoneUtil(this);

		displayUtil = new DisplayUtil(this);

		validator = new Validator();

		userNameTextView = (EditText) findViewById(R.id.user_name);
		userNameTextView.setText(getUserName());

		userPasswordEditText = (EditText) findViewById(R.id.user_password);

		captchaEditText = (EditText) findViewById(R.id.verification_code);

		aotuLoginCheckBox = (CheckBox) findViewById(R.id.auto_login);

		verificationCodeTextView = (TextView) findViewById(R.id.get_verification_code);
		verificationCodeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setVerificationCode();
			}
		});

		setVerificationCode();

		Button findPassword = (Button) findViewById(R.id.find_password);
		findPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFindPasswordAlertDialog();
			}

		});

		Button register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAnnouncementDialog();
				// 升级中，暂时不能登录
				/*
				 * Intent intent = new Intent(LoginActivity.this,
				 * RegisterActivity.class); intent.putExtra("invite", invite);
				 * startActivity(intent);
				 */
			}
		});

		Button login = (Button) findViewById(R.id.login);

		if (((ExpandApplication) getApplication()).isDebug()) {
			login.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Intent intent = new Intent(LoginActivity.this,
							RentCarWithMapAbcActivity.class);
					startActivity(intent);
					return true;
				}
			});
		}

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 显示连接信息
				if ("showmetheversion".equalsIgnoreCase(userNameTextView
						.getText().toString())) {
					String msg = getString(R.string.domain) + "\n";
					msg += getString(R.string.tcp_ip) + "\n";
					msg += getString(R.string.tcp_port);
					AlertDialog.Builder info = new AlertDialog.Builder(
							LoginActivity.this);
					info.setMessage(msg);
					info.setPositiveButton(R.string.confirm, null);
					info.show();
				}

				// showAnnouncementDialog();
				// 升级中，暂时不能登录
				/*
				 * if (!validateLoginInfo()) return; progressDialog =
				 * ProgressDialog.show(LoginActivity.this, null,
				 * getString(R.string.logining), true, false);
				 */
				login();
			}
		});

		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitApp();
			}
		});

		isAbsentSim();
	}

	/**
	 * @version 2.0.0
	 */
	private void showAnnouncementDialog() {
		String message = "系统升级中，敬请期待！";
		AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.confirm, null);
		dialog.show();
	}

	private void showFindPasswordAlertDialog() {
		LinearLayout linearLayout = new LinearLayout(LoginActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		linearLayout.setPadding(10, 0, 10, 0);

		final EditText emailEditText = new EditText(LoginActivity.this);
		emailEditText.setHint(R.string.email_hint);
		emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						100) });

		final EditText phoneEditText = new EditText(LoginActivity.this);
		phoneEditText.setHint(R.string.phone_hint);
		phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
		phoneEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });

		linearLayout.addView(emailEditText);
		linearLayout.addView(phoneEditText);

		new AlertDialog.Builder(LoginActivity.this)
				.setTitle("发送密码找回邮件到注册邮箱")
				.setView(linearLayout)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									int whichButton) {
								ViewUtil.setupDialogNotDismiss(dialog);

								if (!ValidatorUtil.validateEmail(
										LoginActivity.this, emailEditText,
										validator)
										|| !ValidatorUtil.validatePhoneNumber(
												LoginActivity.this,
												phoneEditText, validator)) {
									return;
								}

								final ProgressDialog progressDialog = ProgressDialog
										.show(LoginActivity.this, null,
												getString(R.string.waitting),
												true, true);

								new EndpointClient(LoginActivity.this) {
									@Override
									protected Map<String, Object> doInBackground(
											Void... arg0) {
										Map<String, String> info = new HashMap<String, String>();
										info.put("email", emailEditText
												.getText().toString());
										info.put("mobile", phoneEditText
												.getText().toString());

										return findPassword(info);
									}

									@Override
									protected void onEndpointClientPostExecute(
											Map<String, Object> result) {
										progressDialog.dismiss();

										if (null == result) {
											Toast.makeText(LoginActivity.this,
													R.string.request_fail,
													Toast.LENGTH_LONG).show();

											return;
										}

										if (!(Boolean) result.get("success")) {
											Toast.makeText(
													LoginActivity.this,
													(String) result
															.get("message"),
													Toast.LENGTH_LONG).show();

											return;
										}

										Toast.makeText(
												LoginActivity.this,
												getString(R.string.request_success)
														+ " 密码找回邮件已发往您的邮箱，请查收",
												Toast.LENGTH_LONG).show();

										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}

										dialog.dismiss();
									}
								}.execute();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ViewUtil.setupDialogCanDismiss(dialog);

								dialog.dismiss();
							}
						}).show();
	}

	private void isAbsentSim() {
		boolean absentSim = phoneUtil.isAbsentSim();
		Log.d(tag, "isAbsentSim=" + absentSim);
		if (absentSim) {
			AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
			b.setIcon(R.drawable.alert);
			b.setTitle("没有找到sim卡");
			b.setMessage("请插入sim卡后点击确定");
			b.setCancelable(false);
			b.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							checkAppVersion();
						}
					});
			b.setNegativeButton(R.string.exit_app,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							exitApp();
						}
					});
			b.show();
		} else {
			checkAppVersion();
		}
	}

	private void checkAppVersion() {
		Log.d(tag, "checkAppVersion");
		if (isCheckedApp) {
			autoLogin();
			return;
		}

		progressDialog = ProgressDialog.show(LoginActivity.this, null,
				getString(R.string.loading), true, true,
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						exitApp();
					}
				});

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return updateApp();
			}

			@Override
			protected void onEndpointClientPostExecute(
					final Map<String, Object> result) {
				progressDialog.dismiss();

				Log.d(tag,
						"checkAppVersion.EndpointClient.onEndpointClientPostExecute");
				Log.d(tag, result.toString());

				/**/
				if (null == result || !(Boolean) result.get("success")) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							LoginActivity.this);

					alertDialogBuilder.setIcon(R.drawable.alert);
					alertDialogBuilder.setTitle(R.string.server_busy);
					alertDialogBuilder.setMessage(R.string.confirm_retry);
					alertDialogBuilder.setCancelable(true);
					alertDialogBuilder.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									checkAppVersion();
								}
							});
					alertDialogBuilder.setNegativeButton(R.string.exit_app,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									exitApp();
								}
							});
					alertDialogBuilder
							.setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									checkAppVersion();
								}
							});

					alertDialogBuilder.show();

					return;
				}

				if (null == result.get("invite")) {
					checkAppVersion();
					return;
				}

				invite = (Boolean) result.get("invite");

				sendCrashReport();

				if (!(Boolean) result.get("isUpdate")) {
					autoLogin();
					return;
				}

				Builder alertDialogBuilder = new AlertDialog.Builder(
						LoginActivity.this);
				alertDialogBuilder.setIcon(R.drawable.ic_menu_more);
				alertDialogBuilder.setTitle("发现新版本");
				alertDialogBuilder.setMessage((String) result
						.get("versionInfo"));
				alertDialogBuilder.setCancelable(true);
				alertDialogBuilder.setPositiveButton("立即更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ViewUtil.setupDialogNotDismiss(dialog);

								if (Settings.Secure
										.getInt(getContentResolver(),
												Settings.Secure.INSTALL_NON_MARKET_APPS,
												0) == 0) {
									new AlertDialog.Builder(LoginActivity.this)
											.setIcon(R.drawable.alert)
											.setTitle("更新提示")
											.setMessage("请先在应用程序设置中勾选“未知来源”选项")
											.setPositiveButton(
													"应用程序设置",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int whichButton) {
															Intent intent = new Intent();
															intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);

															startActivity(intent);
														}
													})
											.setNegativeButton(R.string.cancel,
													null).setCancelable(false)
											.show();

									return;
								}

								new AutoUpdater(LoginActivity.this, result
										.get("name") + ".apk", (String) result
										.get("appUrl")).showDownloadDialog();
							}
						});

				if (!(Boolean) result.get("forceUpdateOriginal")) {
					alertDialogBuilder.setNegativeButton("稍后更新",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									isCheckedApp = true;

									ViewUtil.setupDialogCanDismiss(dialog);

									autoLogin();
								}
							});
				} else {
					alertDialogBuilder.setNegativeButton(R.string.exit_app,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									exitApp();
								}
							});
				}

				alertDialogBuilder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();

						checkAppVersion();
					}
				});

				alertDialogBuilder.show();
			}
		}.execute();
	}

	private void autoLogin() {
		String cookie = getCookie();
		if (AutoLoginUtil.isAutoLogin(this) && null != cookie
				&& !"".equals(cookie.trim())) {
			progressDialog = ProgressDialog.show(LoginActivity.this,
					getString(R.string.auto_login),
					getString(R.string.logining), true, true,
					new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							exitApp();
						}
					});

			BasicClientCookie rmCookie = new BasicClientCookie(
					SPRING_SECURITY_REMEMBER_ME_COOKIE, cookie);

			try {
				rmCookie.setDomain(new URL(getString(R.string.domain))
						.getHost());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			application.getCookieStore().addCookie(rmCookie);

			// startActivity(new Intent(LoginActivity.this,
			// DistributorActivity.class));
			startActivity(new Intent(LoginActivity.this, MainTab.class));
			progressDialog.dismiss();
		}
	}

	private void sendCrashReport() {
		final String crashReport = ((ExpandApplication) getApplication())
				.getCrashReport();

		if (null == crashReport || "".equals(crashReport.trim())) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> info = new HashMap<String, String>();
				info.put("errorInfo", crashReport);

				Map<String, Object> result = ((ExpandApplication) getApplicationContext())
						.getDefaultSimpleHttpClient()
						.fetchJsonObject(
								getString(R.string.domain)
										+ getString(R.string.send_crash_report_url),
								info);

				if (null == result || !(Boolean) result.get("success")) {
					return;
				}

				((ExpandApplication) getApplication()).clearCrashReport();
			}
		}).start();
	}

	private void login() {
		this.progressDialog = new ProgressDialog(this);
		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> loginInfo = new HashMap<String, String>();
				loginInfo.put("j_username", userNameTextView.getText()
						.toString());
				loginInfo.put("j_password", userPasswordEditText.getText()
						.toString());
				loginInfo.put("j_captcha_response", captchaEditText.getText()
						.toString());

				if (aotuLoginCheckBox.isChecked()) {
					loginInfo.put("_spring_security_remember_me", "true");
				}

				return login(loginInfo);
			}

			@Override
			protected void onEndpointClientPostExecute(
					Map<String, Object> result) {
				if (null == result) {
					progressDialog.dismiss();

					Toast.makeText(LoginActivity.this, R.string.login_fail,
							Toast.LENGTH_LONG).show();

					return;
				}

				Boolean success = (Boolean) result.get("success");

				DebugUtil.print(result, getClass());

				if (true == success) {
					HashMap<String, Object> msg = (HashMap<String, Object>) result
							.get("message");
					String realName = (String) msg.get("realName");
					saveUserName(realName);

					saveCookie();

					if (aotuLoginCheckBox.isChecked()) {
						AutoLoginUtil.enableAutoLogin(LoginActivity.this);
					} else {
						AutoLoginUtil.unableAutoLogin(LoginActivity.this);
					}

					Toast.makeText(LoginActivity.this, R.string.login_success,
							Toast.LENGTH_SHORT).show();

					// startActivity(new Intent(LoginActivity.this,
					// DistributorActivity.class));
					startActivity(new Intent(LoginActivity.this, MainTab.class));
					progressDialog.dismiss();
				} else {
					setVerificationCode();

					progressDialog.dismiss();

					Toast.makeText(LoginActivity.this,
							(String) result.get("message"), Toast.LENGTH_LONG)
							.show();
				}
			}
		}.execute();
	}

	private void setVerificationCode() {
		verificationCodeTextView.setEnabled(false);
		verificationCodeTextView.setTextColor(getResources().getColor(
				R.color.ivory));

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				return getCaptcha();
			}

			@Override
			protected void onEndpointClientPostExecute(
					Map<String, Object> result) {
				verificationCodeTextView.setEnabled(true);
				verificationCodeTextView.setTextColor(getResources().getColor(
						R.color.dark));

				if (null != result.get("captcha")) {
					Drawable verificationCodeImage = displayUtil
							.bytes2Drawable((byte[]) result.get("captcha"));
					verificationCodeImage.setBounds(0, 0,
							displayUtil.dip2px(106), displayUtil.dip2px(42));
					verificationCodeTextView.setCompoundDrawables(null,
							verificationCodeImage, null, null);

					captchaEditText.setText("");
				}
			}
		}.execute();
	}

	private boolean validateLoginInfo() {
		if (null == userNameTextView.getText().toString()
				|| "".equals(userNameTextView.getText().toString().trim())) {
			Toast.makeText(this, R.string.user_name_hint, Toast.LENGTH_SHORT)
					.show();

			userNameTextView.setError(getString(R.string.user_name_hint));

			return false;
		}

		if (!validator.validateEmail(userNameTextView.getText().toString())
				&& !validator.validatePhoneNumber(userNameTextView.getText()
						.toString())) {
			Toast.makeText(this, "邮箱地址或手机号码验证不合法", Toast.LENGTH_SHORT).show();

			userNameTextView.setError("邮箱地址或手机号码验证不合法");

			return false;
		}

		if (null == userPasswordEditText.getText().toString()
				|| userPasswordEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(this, R.string.user_password_hint,
					Toast.LENGTH_SHORT).show();

			userPasswordEditText
					.setError(getString(R.string.user_password_hint));

			return false;
		}

		if (null == captchaEditText.getText().toString()
				|| "".equals(captchaEditText.getText().toString().trim())) {
			Toast.makeText(this, R.string.verification_code_hint,
					Toast.LENGTH_SHORT).show();

			captchaEditText
					.setError(getString(R.string.verification_code_hint));

			return false;
		}

		return true;
	}

	private String getCookie() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		return sharedPreferences.getString(SPRING_SECURITY_REMEMBER_ME_COOKIE,
				null);
	}

	private SharedPreferences getSharedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				getApplicationInfo().name, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	private String getUserName() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		return sharedPreferences.getString(USER_NAME, "");
	}

	private void saveUserName(String realName) {
		SharedPreferences sharedPreferences = getSharedPreferences();

		Editor editor = sharedPreferences.edit();
		editor.putString(USER_NAME, userNameTextView.getText().toString());
		editor.putString(REAL_NAME, realName);
		editor.commit();
	}

	private void saveCookie() {
		Cookie cookie = application
				.getCookieByName(SPRING_SECURITY_REMEMBER_ME_COOKIE);

		if (null == cookie) {
			return;
		}

		SharedPreferences sharedPreferences = getSharedPreferences();

		Editor editor = sharedPreferences.edit();
		editor.putString(SPRING_SECURITY_REMEMBER_ME_COOKIE, cookie.getValue());
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		exitApp();
	}

	private void exitApp() {
		new AlertDialog.Builder(LoginActivity.this)
				.setIcon(R.drawable.ic_menu_more)
				.setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (null != progressDialog) {
									progressDialog.dismiss();
								}

								((ExpandApplication) getApplication())
										.exitApp();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Log.d(tag, "exitApp.checkAppVersion");
								checkAppVersion();
							}
						}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}
}