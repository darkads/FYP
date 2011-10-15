package com.ntu.fypshop;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;
//import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
import com.ntu.fypshop.ConnectDB.MyVO;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
//import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MapResult extends MapActivity {

	private static final Integer INIT_NORM = 0, INIT_FB = 1;
	private static final Integer MY_POINT = 0, STORES_LOC = 1;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private MapView mapView;
	private MapController mapController;
	private Button logout;
	private static GlobalVariable globalVar;
	Handler mHandler = new Handler();

	private Boolean fbBtn;

	// List<MyVO> myVO;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mapresult);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		mapView = (MapView) findViewById(R.id.mapView);
		logout = (Button) findViewById(R.id.logoutButton);
		globalVar = ((GlobalVariable) getApplicationContext());
		fbBtn = globalVar.getfbBtn();
		Log.d("FbButton: ", fbBtn.toString());
		if (!fbBtn)
		{
			initLogout(INIT_NORM);
		}
		else
		{
			initLogout(INIT_FB);
		}

		// enable Street view by default
		// Problem: Enabling this part triggers cross marks in the map
		// mapView.setStreetView(true);

		// enable to show Satellite view
		// mapView.setSatellite(true);

		// enable to show Traffic on map
		// mapView.setTraffic(true);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(18);
	}

	private void initLogout(final int type)
	{
		// TODO Auto-generated method stub
		logout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// TODO Auto-generated method stub
				// Logout logic here...
				// globalVar = ((GlobalVariable) getApplicationContext());
				// globalVar.setName("");
				// globalVar.setfbBtn(false);
				// globalVar.setHashPw("");
				// globalVar.setEm("");
				//
				// SharedPreferences login =
				// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
				// SharedPreferences.Editor editor = login.edit();
				// editor.putString("emailLogin", "");
				// editor.putString("pwLogin", "");
				// editor.commit();
				//
				// Intent intent = new Intent(v.getContext(), LoginPage.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				doLogout(type);
			}
		});
	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private void doLogout(int type)
	{
		if (type == INIT_NORM)
		{
			// Logout logic here...
			globalVar = ((GlobalVariable) getApplicationContext());
			globalVar.setName("");
			globalVar.setfbBtn(false);
			globalVar.setHashPw("");
			globalVar.setEm("");

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("emailLogin", "");
			editor.putString("pwLogin", "");
			editor.commit();
		}
		else
		{
			// Go to login
			globalVar = ((GlobalVariable) getApplicationContext());
			Facebook mFacebook = globalVar.getFBState();
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
			asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
		}

		// Return to the login activity
		Intent intent = new Intent(this, LoginPage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private class GPSLocationListener implements LocationListener {

		GlobalVariable globalVar = ((GlobalVariable) getApplicationContext());

		// private ArrayList<Markers> mapOverlays = new ArrayList<Markers>();
		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{
				GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
				/*
				 * Toast.makeText(getBaseContext(), "Latitude: " +
				 * location.getLatitude() + " Longitude: " +
				 * location.getLongitude(), Toast.LENGTH_SHORT).show();
				 */

				mapController.animateTo(point);
				mapController.setZoom(18);

				// add marker
				MapOverlay mapOverlay = new MapOverlay(MY_POINT);
				mapOverlay.setPointToDraw(point);
				List<Overlay> listOfOverlays = mapView.getOverlays();
				listOfOverlays.clear();
				listOfOverlays.add(mapOverlay);

				String address = ConvertPointToLocation(point);
				Toast.makeText(MapResult.this, address, Toast.LENGTH_LONG).show();

				// Drawable drawable =
				// getResources().getDrawable(R.drawable.red);

				if (globalVar.getSearchType() == 1)
				{
					ConnectDB connect = new ConnectDB(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 500);

					Log.d("Store Location Results in activity: ", connect.storeLocResult());
					// Markers usersMarker = new
					// Markers(drawable,MapResult.this);
					for (MyVO vo : connect.getMyVO())
					{
						Log.d("VO address: ", vo.address);
						Log.d("VO name: ", vo.name);
						Log.d("VO lat: ", vo.lat);
						Log.d("VO lng: ", vo.lng);
						Log.d("VO distance: ", vo.distance);

						GeoPoint p = new GeoPoint((int) (Double.parseDouble(vo.lat) * 1E6), (int) (Double.parseDouble(vo.lng) * 1E6));
						// Log.d("VO lat after geopoint: ",Integer.toString(((int)
						// (Double.parseDouble(vo.lat) * 1E6))));
						// OverlayItem item = new OverlayItem(p,"Testing Title",
						// "Testing Description");
						// item.setMarker(drawable);
						// usersMarker.addOverlay(item);
						MapOverlay mapOverlay2 = new MapOverlay(STORES_LOC);
						mapOverlay2.setPointToDraw(p);
						listOfOverlays.add(mapOverlay2);
					}
				}

				// mapOverlays.add(usersMarker);

				mapView.invalidate();
			}
		}

		public String ConvertPointToLocation(GeoPoint point)
		{
			String address = "";
			Geocoder geoCoder = new Geocoder(MapResult.this, Locale.getDefault());
			try
			{
				List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);

				if (addresses.size() > 0)
				{
					Log.d("In if: ", "Hello");
					for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
					{
						address += addresses.get(0).getAddressLine(index) + " ";
					}
				}
				// else
				// {
				// Log.d("address = 0: ", Double.toString(point.getLatitudeE6()
				// / 1E6));
				// address = "Latitude: " + (point.getLatitudeE6() / 1E6) +
				// "\n Longtitude: " + (point.getLongitudeE6() / 1E6);
				// }
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.d("address = 0: ", Double.toString(point.getLatitudeE6() / 1E6));
				address = "Latitude: " + (point.getLatitudeE6() / 1E6) + "\nLongtitude: " + (point.getLongitudeE6() / 1E6);
				// address = "Latitude: " + point.getLatitudeE6() +
				// "/nLongtitude: " + point.getLongitudeE6();
			}

			return address;
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}

	}

	class MapOverlay extends Overlay {
		private GeoPoint pointToDraw;
		private Integer drawIcon;

		public MapOverlay(Integer type)
		{
			if (type == MY_POINT)
			{
				drawIcon = R.drawable.red;
			}
			else if (type == STORES_LOC)
			{
				drawIcon = R.drawable.marker;
			}
		}

		public void setPointToDraw(GeoPoint point)
		{
			pointToDraw = point;
		}

		public GeoPoint getPointToDraw()
		{
			return pointToDraw;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
		{
			super.draw(canvas, mapView, shadow);

			// convert point to pixels
			Point screenPts = new Point();
			mapView.getProjection().toPixels(pointToDraw, screenPts);

			// add marker
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawIcon);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 15, null); // 15
																			// is
																			// the
																			// height
																			// of
																			// image
			return true;
		}
	}

	public class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, final Object state)
		{

			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable()
			{
				public void run()
				{
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	// public class Markers extends ItemizedOverlay<OverlayItem> {
	//
	// private Context ctx;
	//
	// private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	//
	// public Markers(Drawable defaultMarker, Context cont)
	// {
	//
	// super(boundCenterBottom(defaultMarker));
	// this.ctx = cont;
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// protected OverlayItem createItem(int i)
	// {
	// // TODO Auto-generated method stub
	// return mOverlays.get(i);
	// }
	//
	// @Override
	// public boolean onTap(GeoPoint p, MapView mapView)
	// {
	// // TODO Auto-generated method stub
	// return super.onTap(p, mapView);
	// }
	//
	// @Override
	// protected boolean onTap(int index)
	// {
	// // TODO Auto-generated method stub
	// // Toast.makeText(this.ctx,
	// // mOverlays.get(index).getTitle().toString() + ", Latitude: " +
	// // mOverlays.get(index).getPoint().getLatitudeE6(),
	// // Toast.LENGTH_SHORT).show();
	// return super.onTap(index);
	// }
	//
	// @Override
	// public int size()
	// {
	// // TODO Auto-generated method stub
	// return mOverlays.size();
	// }
	//
	// public void addOverlay(OverlayItem item)
	// {
	// mOverlays.add(item);
	// setLastFocusedIndex(-1);
	// populate();
	//
	// }
	//
	// public void clear()
	// {
	// mOverlays.clear();
	// setLastFocusedIndex(-1);
	// populate();
	// }
	// }
}
