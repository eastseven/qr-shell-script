package com.quickride.customer.trans.view;

import ac.mm.android.view.MPopupWindow;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;

import com.quickride.customer.R;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-11-18
 * @version 1.0
 */

public class MenuButton extends ImageButton {
	private MPopupWindow menu;

	public MenuButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		setImageResource(R.drawable.menu_white);

		menu = new MPopupWindow(new MenuGridView(context), this, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) {
			@Override
			public void show() {
				// FIXME yoff为1或2会导致死机
				showAsDropDown(MenuButton.this, 0, 3);
			}
		};

		menu.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_bg));
	}

	public boolean isShowing() {
		return menu.isShowing();
	}

	public void dismiss() {
		menu.dismiss();
	}
}
