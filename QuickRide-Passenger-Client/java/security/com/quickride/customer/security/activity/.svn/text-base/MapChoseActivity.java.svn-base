package com.quickride.customer.security.activity;

import ac.mm.android.activity.GestureSwitchPageActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.quickride.customer.trans.activity.RentCarWithGoogleMapActivity;
import com.quickride.customer.trans.activity.RentCarWithMapAbcActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-2
 * @version 1.0
 */

public class MapChoseActivity extends GestureSwitchPageActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout rootLayout = new LinearLayout(this);
		rootLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		Button mapAbcButton = new Button(this);
		mapAbcButton.setText("Map ABC");
		mapAbcButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mapAbcButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MapChoseActivity.this, RentCarWithMapAbcActivity.class);

				MapChoseActivity.this.startActivity(intent);
			}
		});

		Button googleMapButton = new Button(this);
		googleMapButton.setText("Google Map");
		googleMapButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		googleMapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MapChoseActivity.this, RentCarWithGoogleMapActivity.class);

				MapChoseActivity.this.startActivity(intent);
			}
		});

		rootLayout.addView(mapAbcButton);
		rootLayout.addView(googleMapButton);

		setContentView(rootLayout);
	}
}
