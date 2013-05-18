package com.quickride.customer.trans.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.PoiOverlay;
import com.quickride.customer.R;

public class MyPoiOverlayWithMapAbc extends PoiOverlay {
	private Context context;
	private List<PoiItem> poiItems;
	private LayoutInflater mInflater;
	private int height;
	private int popupLayout = R.layout.overlay_popup;

	public MyPoiOverlayWithMapAbc(Context context, Drawable drawable, List<PoiItem> poiItems) {
		super(drawable, poiItems);

		this.context = context;
		this.poiItems = poiItems;
		mInflater = LayoutInflater.from(context);
		height = drawable.getIntrinsicHeight();
	}

	@Override
	protected Drawable getPopupBackground() {
		return context.getResources().getDrawable(R.drawable.bubble_background);
	}

	@Override
	protected View getPopupView(final PoiItem item) {
		View view = mInflater.inflate(popupLayout, null);
		TextView title = (TextView) view.findViewById(R.id.map_bubble_title);
		TextView addressTextView = (TextView) view.findViewById(R.id.map_bubble_text);
		title.setText(item.getTitle());
		String address = item.getTypeDes();

		addressTextView.setText(address);

		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.map_bubble);
		layout.setOnClickListener(getOnClickBubbleListener());

		return view;
	}

	protected View.OnClickListener getOnClickBubbleListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		};
	}

	@Override
	protected LayoutParams getLayoutParam(int index) {
		LayoutParams params = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, poiItems.get(index).getPoint(), 0, -height,
				LayoutParams.BOTTOM_CENTER);

		return params;
	}

	public void setPopupLayout(int popupLayout) {
		this.popupLayout = popupLayout;
	}
}