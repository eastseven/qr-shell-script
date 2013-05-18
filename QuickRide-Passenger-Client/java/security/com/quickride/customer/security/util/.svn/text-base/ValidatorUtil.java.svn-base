package com.quickride.customer.security.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ac.mm.android.util.security.Validator;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.quickride.customer.R;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-11
 * @version 1.0
 */

public class ValidatorUtil {

	public static boolean validatePhoneNumber(Context context, TextView phoneEditText, Validator validator) {
		String phoneNumber = phoneEditText.getText().toString();

		if (null == phoneNumber || "".equals(phoneNumber.trim())) {
			Toast.makeText(context, R.string.phone_hint, Toast.LENGTH_SHORT).show();

			phoneEditText.setError(context.getString(R.string.phone_hint));

			return false;
		}

		if (!validator.validatePhoneNumber(phoneNumber)) {
			Toast.makeText(context, "手机号码验证不合法", Toast.LENGTH_SHORT).show();

			phoneEditText.setError("手机号码验证不合法");

			return false;
		}

		return true;
	}

	public static boolean validateEmail(Context context, TextView emailEditText, Validator validator) {
		String email = emailEditText.getText().toString();

		if (null == email || "".equals(email.trim())) {
			Toast.makeText(context, R.string.email_hint, Toast.LENGTH_SHORT).show();

			emailEditText.setError(context.getString(R.string.email_hint));

			return false;
		}

		if (!validator.validateEmail(email)) {
			Toast.makeText(context, "邮箱地址验证不合法", Toast.LENGTH_SHORT).show();

			emailEditText.setError("邮箱地址验证不合法");

			return false;
		}

		return true;
	}

	public static boolean validatePassword(Context context, TextView passwordEditText) {
		String password = passwordEditText.getText().toString();

		if (null == password || password.trim().length() < 6) {
			Toast.makeText(context, "密码至少6位", Toast.LENGTH_SHORT).show();

			passwordEditText.setError("密码至少6位");

			return false;
		}

		Pattern pat = Pattern.compile("[\u4E00-\u9FA5]+");
		Matcher matcher = pat.matcher(password);
		if (matcher.find()) {
			Toast.makeText(context, "密码不能包含中文", Toast.LENGTH_SHORT).show();

			passwordEditText.setError("密码不能包含中文");

			return false;
		}

		return true;
	}

	public static boolean validatePassword(Context context, TextView passwordEditText, TextView otherPasswordEditText) {
		if (!validatePassword(context, passwordEditText)) {
			return false;
		}

		String password = passwordEditText.getText().toString();
		String otherPassword = otherPasswordEditText.getText().toString();

		if (!password.equals(otherPassword)) {
			Toast.makeText(context, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();

			otherPasswordEditText.setError("两次输入的密码不一致");

			return false;
		}

		return true;
	}

	public static boolean validateUserName(Context context, TextView nameEditText, Validator validator) {
		String name = nameEditText.getText().toString();

		if (null == name || "".equals(name.trim())) {
			Toast.makeText(context, R.string.name_hint, Toast.LENGTH_SHORT).show();

			nameEditText.setError(context.getString(R.string.name_hint));

			return false;
		}

		if ("zh".equals(context.getResources().getConfiguration().locale.getLanguage())) {
			if (!validator.validateChinese(name)) {
				Toast.makeText(context, "姓名必须全为中文", Toast.LENGTH_SHORT).show();

				nameEditText.setError("姓名必须全为中文");

				return false;
			}
		}

		return true;
	}
}
