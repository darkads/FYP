package com.ntu.fypshop;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class ShopPage extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private GeoPoint gp;
	private GlobalVariable globalVar;
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	MapItemizedOverlay itemizedOverlay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shop);
		// TODO Auto-generated method stub

		mapView = (MapView) findViewById(R.id.shopMap);
		globalVar = ((GlobalVariable) getApplicationContext());

		gp = globalVar.getGeoPoint();
		
		mapView.setBuiltInZoomControls(false);
		mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.setCenter(gp);
		
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.pushpin);
		itemizedOverlay = new MapItemizedOverlay(drawable);
		
		OverlayItem overlayitem = new OverlayItem(gp, "", "");
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.clear();
		mapOverlays.add(itemizedOverlay);
		mapView.invalidate();
	}	

	class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		public MapItemizedOverlay(Drawable defaultMarker)
		{
			super(boundCenterBottom(defaultMarker));
			// TODO Auto-generated constructor stub
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
