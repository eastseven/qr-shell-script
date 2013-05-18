package com.quickride.customer.trans.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.Overlay;
import com.mapabc.mapapi.map.Projection;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-7-16
 * @version 1.0
 */

public class RectBoxOverlay extends Overlay {
	private GeoPoint southwestGeoPoint, northeastGeoPoint;
	private int color;

	public RectBoxOverlay(GeoPoint southwestGeoPoint, GeoPoint northeastGeoPoint, int color) {
		this.southwestGeoPoint = southwestGeoPoint;
		this.northeastGeoPoint = northeastGeoPoint;
		this.color = color;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean arg2) {
		super.draw(canvas, mapView, arg2);

		Projection projection = mapView.getProjection();

		Paint paint = new Paint();
		paint.setColor(color);
		// paint.setAlpha(30);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);

		Point southwestPixels = projection.toPixels(southwestGeoPoint, null);
		Point northeastPixels = projection.toPixels(northeastGeoPoint, null);

		canvas.drawRect(new RectF(southwestPixels.x, northeastPixels.y, northeastPixels.x, southwestPixels.y), paint);
	}
}
