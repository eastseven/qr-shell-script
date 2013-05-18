package com.quickride.customer.ui.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickride.customer.R;

public class OrderProductAdapter extends ArrayAdapter<HashMap<String, Object>> {

	private static final String tag = "OrderProductAdapter";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private ArrayList<HashMap<String, Object>> orderProducts;
	
	public OrderProductAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> objects) {
		super(context, textViewResourceId, objects);
		this.orderProducts = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Log.d(tag, "convertView="+convertView+", parent="+parent);
		if(view == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.main_order_product_item, null);
		}

		HashMap<String, Object> orderProduct = orderProducts.get(position);
		
		ImageView image = (ImageView) view.findViewById(R.id.main_order_product_item_image);
		TextView text0  = (TextView)  view.findViewById(R.id.main_order_product_item_text_0);
		TextView text1  = (TextView)  view.findViewById(R.id.main_order_product_item_text_1);
		TextView text2  = (TextView)  view.findViewById(R.id.main_order_product_item_text_2);
		TextView text3  = (TextView)  view.findViewById(R.id.main_order_product_item_text_3);
		
		String _uri = orderProduct.get("productImageUrl").toString();
		Bitmap bm = getHttpBitmap(_uri);
		image.setImageBitmap(bm);
		
		text0.setText(orderProduct.get("gradeDesc").toString());
		text1.setText(orderProduct.get("name").toString());
		text2.setText(orderProduct.get("address").toString());
		text3.setText(sdf.format(new Date((Long) orderProduct.get("pickupTime"))));
		
		return view;
	}
	
	@Override
	public HashMap<String, Object> getItem(int position) {
		return orderProducts.get(position);
	}
	
	public static Bitmap getHttpBitmap(String url) {
		URL _url;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			_url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return bitmap;
	}
}
