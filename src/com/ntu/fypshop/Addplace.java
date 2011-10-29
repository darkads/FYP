package com.ntu.fypshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.ntu.fypshop.MapResult.Markers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Addplace extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private GeoPoint startingPoint;
	private EditText mapSearchBox, searchAddress;
	private GlobalVariable globalVar;
	private Button search;
	List<Overlay> listOfOverlays;

	List<String> addrList = null;
	HashMap<GeoPoint, String> addrMap;
	String address = "";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplace);

		mapView = (MapView) findViewById(R.id.addShopMap);
		mapSearchBox = (EditText) findViewById(R.id.txtShopname);
		searchAddress = (EditText) findViewById(R.id.txtShopaddress);
		search = (Button) findViewById(R.id.searchShopsBtn);

		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);

		startingPoint = new GeoPoint(1303999, 103832731);
		mapController.setCenter(startingPoint);

		init();
	}

	private void init()
	{
		// TODO Auto-generated method stub
		globalVar = ((GlobalVariable) getApplicationContext());
		search.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (listOfOverlays != null)
				{
					clearBalloon();
					listOfOverlays.clear();
				}
				if (!doSearch(mapSearchBox.getText().toString()))
				{
					Toast.makeText(Addplace.this, "Unable to find any result. Please try again.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					mapController.animateTo(startingPoint);
				}
				// new
				// SearchClicked(mapSearchBox.getText().toString());//.execute();
			}
		});
		// mapSearchBox.setOnEditorActionListener(new
		// TextView.OnEditorActionListener()
		// {
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event)
		// {
		// if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId ==
		// EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
		// event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() ==
		// KeyEvent.KEYCODE_ENTER)
		// {
		//
		// // hide virtual keyboard
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
		//
		// new SearchClicked(mapSearchBox.getText().toString()).execute();
		// mapSearchBox.setText("", TextView.BufferType.EDITABLE);
		// return true;
		// }
		// return false;
		// }
		// });
	}

	private class SearchClicked {// extends AsyncTask<Void, Void, Boolean> {
		private String toSearch;
		private Address address;

		public SearchClicked(String toSearch)
		{
			this.toSearch = toSearch;
			// doSearch();
		}

		// protected Boolean doSearch()
		// {
		// try
		// {
		// Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
		// List<Address> results = geocoder.getFromLocationName(toSearch, 20);
		// Drawable drawableItem =
		// getResources().getDrawable(R.drawable.pushpin);
		// Markers itemmarker = new Markers(drawableItem, mapView);
		//
		// if (results.size() == 0)
		// {
		// return false;
		// }
		//
		// for (int i = 0; i < results.size(); i++)
		// {
		// address = results.get(i);
		//
		// // Now do something with this GeoPoint:
		// GeoPoint p = new GeoPoint((int) (address.getLatitude() * 1E6), (int)
		// (address.getLongitude() * 1E6));
		// OverlayItem item = new OverlayItem(p, "", "");
		// // item.setMarker(getResources().getDrawable(R.drawable.pushpin));
		// itemmarker.addOverlay(item);
		// }
		//
		// }
		// catch (Exception e)
		// {
		// Log.e("", "Something went wrong: ", e);
		// Toast.makeText(Addplace.this,
		// "Oops Google Maps Service is not available at this moment.",
		// Toast.LENGTH_SHORT);
		// return false;
		// }
		// return true;
		// }

		// @Override
		// protected Boolean doInBackground(Void... voids)
		// {
		//
		// try
		// {
		// Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
		// List<Address> results = geocoder.getFromLocationName(toSearch, 20);
		// Drawable drawableItem =
		// getResources().getDrawable(R.drawable.pushpin);
		// Markers itemmarker = new Markers(drawableItem, mapView);
		//
		// if (results.size() == 0)
		// {
		// return false;
		// }
		//
		// for (int i = 0; i < results.size(); i++)
		// {
		// address = results.get(i);
		//
		// // Now do something with this GeoPoint:
		// GeoPoint p = new GeoPoint((int) (address.getLatitude() * 1E6), (int)
		// (address.getLongitude() * 1E6));
		// OverlayItem item = new OverlayItem(p, "", "");
		// // item.setMarker(getResources().getDrawable(R.drawable.pushpin));
		// itemmarker.addOverlay(item);
		// }
		//
		// }
		// catch (Exception e)
		// {
		// Log.e("", "Something went wrong: ", e);
		// Toast.makeText(Addplace.this,
		// "Oops Google Maps Service is not available at this moment.",
		// Toast.LENGTH_SHORT);
		// return false;
		// }
		// return true;
		// }
	}

	private Boolean doSearch(String toSearch)
	{
		Address addressSearch;
		Drawable drawableItem = getResources().getDrawable(R.drawable.pushpin);
		Double lowerLeftLat = 1.253715;
		Double lowerLeftLng = 103.613434;
		Double upperRigLat = 1.482302;
		Double upperRigLng = 104.003448;
		OverlayItem item = null;
		String addr;

		try
		{
			listOfOverlays = mapView.getOverlays();
			listOfOverlays.clear();
			Markers itemmarker = new Markers(drawableItem, mapView);
			Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
			List<Address> results = geocoder.getFromLocationName(toSearch + " Singapore", 100, lowerLeftLat, lowerLeftLng, upperRigLat, upperRigLng);

			for (int l = 0; l < results.size(); l++)
			{
				Log.d("Results: ", results.get(l).toString());
			}
			// addrList = new ArrayList<String>();
			addrMap = new HashMap<GeoPoint, String>();
			if (results.size() == 0)
			{
				return false;
			}

			else
			{
				startingPoint = new GeoPoint((int) (results.get(0).getLatitude() * 1E6), (int) (results.get(0).getLongitude() * 1E6));
				for (int i = 0; i < results.size(); i++)
				{
					addressSearch = results.get(i);
					addr = "";
					String geopoint = Double.toString(addressSearch.getLatitude()) + " " + Double.toString(addressSearch.getLongitude());
					// Now do something with this GeoPoint:
					GeoPoint p = new GeoPoint((int) (addressSearch.getLatitude() * 1E6), (int) (addressSearch.getLongitude() * 1E6));
					if (addressSearch.getMaxAddressLineIndex() == -1)
					{

					}
					else
					{
						for (int j = 1; j <= addressSearch.getMaxAddressLineIndex() - 1; j++)
						{
							addr += addressSearch.getAddressLine(j) + " ";
						}
						addr += addressSearch.getAddressLine(addressSearch.getMaxAddressLineIndex());
						setAddress(p, addr);
						item = new OverlayItem(p, addressSearch.getAddressLine(0), addr);
						itemmarker.addOverlay(item);
					}
				}
				if (itemmarker != null)
				{
					listOfOverlays.add(itemmarker);
				}

				mapView.invalidate();
			}
		}
		catch (Exception e)
		{
			Log.e("", "Something went wrong: ", e);
			Toast.makeText(Addplace.this, "Oops Google Maps Service is not available at this moment.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public String ConvertPointToLocation(GeoPoint point)
	{
		String addressresult = "";
		Geocoder geoCoder = new Geocoder(Addplace.this, Locale.getDefault());
		try
		{
			List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);

			if (addresses.size() > 0)
			{
				Log.d("In if: ", "Hello");
				for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
				{
					addressresult += addresses.get(0).getAddressLine(index) + " ";
				}
			}
			// else
			// {
			// address = "Latitude: " + (point.getLatitudeE6() / 1E6) +
			// "\n Longtitude: " + (point.getLongitudeE6() / 1E6);
			// }
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.d("address = 0: ", Double.toString(point.getLatitudeE6() / 1E6));
			address = "Latitude: " + (point.getLatitudeE6() / 1E6) + "\nLongtitude: " + (point.getLongitudeE6() / 1E6);
		}

		return addressresult;
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public class Markers extends BalloonItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public Markers(Drawable defaultMarker, MapView mv)
		{
			super(boundCenter(defaultMarker), mv, globalVar);

			// this.addressP = addressListPassed;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView)
		{
			// TODO Auto-generated method stub
			clearBalloon();
			
			for (GeoPoint key : addrMap.keySet())
			{
				Log.d("key/value: ", key + "/" + addrMap.get(key));
			}
			return super.onTap(p, mapView);
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem item)
		{
			mOverlays.add(item);
			// setLastFocusedIndex(-1);
			populate();

		}

		public void clear()
		{
			mOverlays.clear();
			// setLastFocusedIndex(-1);
			populate();
		}

		@Override
		public boolean onBalloonTap(int index, OverlayItem item)
		{
			// Intent myintent = new Intent(Addplace.this, ShopPage.class);
			// startActivity(myintent);
			Log.d("Global Variable", globalVar.getGeoPoint().toString());
			address = getAddress(globalVar.getGeoPoint());
			Log.d("Address: ", address);
			searchAddress.setText(address);
			if (address != null)
			{
				clearBalloon();
			}
			return (super.onBalloonTap(index, item));
		}
	}

	public void setAddress(GeoPoint key, String value)
	{
		addrMap.put(key, value);
	}

	public String getAddress(GeoPoint key)
	{
		return addrMap.get(key);
	}
	
	public void clearBalloon()
	{
		if (listOfOverlays != null)
		{
			for (Overlay overlay : listOfOverlays)
			{
				if (overlay instanceof BalloonItemizedOverlay<?>)
				{
					if (((BalloonItemizedOverlay<?>) overlay).balloonView != null)
						((BalloonItemizedOverlay<?>) overlay).balloonView.setVisibility(View.GONE);
				}
			}
		}
	}

}
