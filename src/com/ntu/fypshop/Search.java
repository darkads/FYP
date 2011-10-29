package com.ntu.fypshop;

import java.util.ArrayList;
import java.util.List;

//import org.json.JSONException;
//import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
//import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
//import com.facebook.android.FacebookError;
import com.facebook.android.Util;
//import com.facebook.android.Facebook.DialogListener;
import com.google.android.maps.GeoPoint;
import com.ntu.fypshop.TwitterApp.TwDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity{
	
	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
//	FbConnect fbConnect;

	private static GlobalVariable globalVar;
	private TextView departmentalStores, clothes, others; //name, departmentalStores, clothes, others;
	private Button logout, genSearch, locSearch, productPage;
	private RadioGroup searchType;
	private EditText searchTerm;
	// private UserParticulars userS;
//	private String fnameS;
//	private String lnameS;
//	private String nameS;
	// private String emailS;
	private Boolean fbBtn, twitBtn;
	private Facebook facebook;

	private TwitterApp mTwitter;
	private LocationManager locationManager;
	private GPSLocationListener locationListener;
	private GeoPoint point = new GeoPoint(1304256, 103832538);
	private List<GeoPoint> pointList;

	Handler mHandler = new Handler();
//	private ProgressDialog mProgress;

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;
	private static final String ITEMS = "Items", SHOPS = "store_locations";

	// private String genderS;
	// private String bdayS;

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	// private EditText lname;

	/** Called when the activity is first created. */
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		
    	super.onCreate(savedInstanceState);
    	
		setContentView(R.layout.search);

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}

//		name = (TextView) findViewById(R.id.helloTxt);
		departmentalStores = (TextView) findViewById(R.id.departmentalstores);
		clothes = (TextView) findViewById(R.id.clothes);
		others = (TextView) findViewById(R.id.others);
		logout = (Button) findViewById(R.id.logoutBtn2);
		genSearch = (Button) findViewById(R.id.genSearchBtn);
		locSearch = (Button) findViewById(R.id.locSearchBtn);
		searchType = (RadioGroup) findViewById(R.id.searchTypeRadio);
		productPage = (Button) findViewById(R.id.productPageBtn);
		searchTerm = (EditText) findViewById(R.id.searchText);

		globalVar = ((GlobalVariable) getApplicationContext());
		fbBtn = globalVar.getfbBtn();
		twitBtn = globalVar.getTwitBtn();
		
		facebook = globalVar.getFBState();
		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		globalVar.setTwitState(mTwitter);
		
//		mProgress = new ProgressDialog(this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		Log.d("FbButton: ", fbBtn.toString());
//		SharedPreferences sharedPref = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		
		if (fbBtn || facebook.isSessionValid())
		{
//			fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
			init(INIT_FB);
		}
		
		else if (twitBtn || mTwitter.hasAccessToken())
		{
			init(INIT_TWIT);
			if(mTwitter.hasAccessToken())
			{
//				name.setText("Hello " + sharedPref.getString("user_name", "") + ",");
			}
			else
			{
				globalVar.setTwitBtn(false);
				mTwitter.authorize();
			}
		}
		else
		{
			SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			String Uname = userDetails.getString("emailLogin", "");
			String pass = userDetails.getString("pwLogin", "");
			Log.d("Uname: ", Uname);
			Log.d("Password: ", pass);
			if (Uname == "" && pass.equals(""))
			{
				Intent launchLogin = new Intent(this, LoginPage.class);
				startActivity(launchLogin);
			}
			else
			{
				ConnectDB connectCheck = new ConnectDB(Uname, pass, 1);
				if (connectCheck.inputResult())
				{
//					name.setText("Hello " + connectCheck.getName() + ",");
					init(INIT_NORM);
				}
				else
				{
					// do something else
					Log.d("Authenticate User: ", "False");
					showDialog(DIALOG_ERR_LOGIN);
				}
			}
		}

		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction("com.package.ACTION_LOGOUT");
		// registerReceiver(new BroadcastReceiver()
		// {
		//
		// @Override
		// public void onReceive(Context context, Intent intent)
		// {
		// Log.d("onReceive", "Logout in progress");
		// // At this point you should start the login activity and finish
		// // this one
		// Intent loginIntent = new Intent(SearchShops.this, LoginPage.class);
		// startActivity(loginIntent);
		// finish();
		// }
		// }, intentFilter);
	}

	private void searchStores()
	{
		// TODO Auto-generated method stub
		// if (globalVar.getSearchType() == 1)
		// {
		if(point == null)
		{
			Toast.makeText(this, "Unable to get a gps connection. Is your gps service turned on?", Toast.LENGTH_SHORT);
			return;
		}
		ConnectDB connect = new ConnectDB(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, getSearchType(), searchTerm.getText().toString(), 1000);

		pointList = new ArrayList<GeoPoint>();
		// Log.d("Store Location Results in activity: ",
		// connect.storeLocResult());
		for (Shop sp : connect.getShop())
		{
			// Log.d("Shop address: ", sp.address);
			// Log.d("Shop name: ", sp.name);
			// Log.d("Shop lat: ", sp.lat);
			// Log.d("Shop lng: ", sp.lng);
			// Log.d("Shop distance: ", sp.distance);

			GeoPoint p = new GeoPoint((int) (Double.parseDouble(sp.lat) * 1E6), (int) (Double.parseDouble(sp.lng) * 1E6));
			pointList.add(p);
			// Log.d("VO lat after geopoint: ",Integer.toString(((int)
			// (Double.parseDouble(vo.lat) * 1E6))));
			// OverlayItem item = new OverlayItem(p,"Testing Title",
			// "Testing Description");
			// item.setMarker(drawable);
			// usersMarker.addOverlay(item);
			// MapOverlay mapOverlay2 = new MapOverlay(STORES_LOC);
			// mapOverlay2.setPointToDraw(p);
			// listOfOverlays.add(mapOverlay2);
			// OverlayItem item = new OverlayItem(p, sp.name, sp.address);
			// item.setMarker(getResources().getDrawable(R.drawable.pushpin));

			// }
		}
		Log.d("PointList: ", Integer.toString(pointList.get(0).getLatitudeE6()));
	}

	private void init(final int type)
	{
		globalVar = ((GlobalVariable) getApplicationContext());
		// TODO Auto-generated method stub
		genSearch.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// globalVar.setSearchType(getSearchType());
			}
		});
		locSearch.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				searchStores();
			}
		});
		departmentalStores.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				globalVar.setSearchType(1);
				Intent intent = new Intent(v.getContext(), MapResult.class);
				startActivity(intent);
			}
		});
		clothes.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				globalVar.setSearchType(2);
				Intent intent = new Intent(v.getContext(), Addplace.class);
				startActivity(intent);
			}
		});
		others.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				globalVar.setSearchType(3);
				Intent intent = new Intent(v.getContext(), MapResult.class);
				startActivity(intent);
			}
		});
		productPage.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), ProductPage.class);
				startActivity(intent);
			}
		});
		logout.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				doLogout(type);
			}
		});
	}

	protected String getSearchType()
	{
		// TODO Auto-generated method stub
		if (searchType.getCheckedRadioButtonId() == R.id.itemsRadio)
		{
			return ITEMS;
		}
		else
		{
			return SHOPS;
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		super.onActivityResult(requestCode, resultCode, data);
//		// if (requestCode != 0)
//		// {
//		fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
//		// }
//	}

	private void doLogout(int type)
	{
		if (type == INIT_NORM)
		{
			// Logout logic here...
			globalVar = ((GlobalVariable) getApplicationContext());
			globalVar.setName("");
			globalVar.setHashPw("");
			globalVar.setEm("");

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			Editor editor = login.edit();
			editor.putString("emailLogin", "");
			editor.putString("pwLogin", "");
			editor.commit();
		}
		else if (type == INIT_FB)
		{
			// Go to LoginPage
			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			Editor editor = login.edit();
			editor.putString("facebookName", "");
			editor.commit();
			globalVar = ((GlobalVariable) getApplicationContext());
			Facebook mFacebook = globalVar.getFBState();
			globalVar.setfbBtn(false);
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
			asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
		}
		else
		{
			mTwitter.resetAccessToken();
			globalVar.setTwitBtn(false);
		}

		// Return to the login activity
		Intent intent = new Intent(this, LoginPage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

//			SharedPreferences sharedPref = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
//			name.setText("Hello " + username + ",");
//			name.setText("Hello " + sharedPref.getString("user_name", "") + ",");
			// mTwitterBtn.setText("  Twitter  (" + username + ")");
			// mTwitterBtn.setChecked(true);
			// mTwitterBtn.setTextColor(Color.WHITE);

			// Toast.makeText(TestConnect.this, "Connected to Twitter as " +
			// username, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(String value)
		{
			// mTwitterBtn.setChecked(false);
			//
			// Toast.makeText(TestConnect.this, "Twitter connection failed",
			// Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onCancel()
		{
			// Return to the login activity
			Intent intent = new Intent(Search.this, LoginPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.clothes:
	// showResults("Clothes");
	// return true;
	// case R.id.others:
	// showResults("Others");
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

//	public class FbConnect {
//
//		private final String[] FACEBOOK_PERMISSION =
//		{ "user_birthday", "email", "publish_stream", "read_stream", "offline_access" };
//
//		private Context context;
//		private Activity activity;
////		private Handler mHandler;
//		private Facebook facebook;
//		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());
//		
//		private SharedPreferences sharedPref;
//		private Editor editor;
//
//		// private SessionListener mSessionListener = new SessionListener();
//
//		public FbConnect(String appId, Activity activity, Context context)
//		{
//
//			this.context = context;
////			this.mHandler = new Handler();
//			this.activity = activity;
//			
//			sharedPref = context.getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
//
//			editor = sharedPref.edit();
//			globalVar = ((GlobalVariable) getApplicationContext());
//
//			// SharedPreferences prefs=
//			// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
//			// String access_token = prefs.getString("access_token", null);
//			// Long expires = prefs.getLong("access_expires", -1);
//			// String sharedName = prefs.getString("name", "");
//			//
//			//
//			// if (access_token != null && expires != -1)
//			// {
//			// facebook.setAccessToken(access_token);
//			// facebook.setAccessExpires(expires);
//			// }
//
//			// if (!facebook.isSessionValid() || sharedName.equals(""))
//			// {
//			// facebook.authorize(activity, FACEBOOK_PERMISSION, new
//			// LoginDialogListener());
//			// }
//			// else
//			// {
//			// name.setText("Hello " + sharedName + ",");
//			// }
//			facebook = FbState.getFBState();
//			// if (!facebook.isSessionValid())
//			// {
//			// facebook = new Facebook(APP_ID);
//			// FbState.setFbState(facebook);
//			login();
//			// }
//			// else
//			// {
//			// SessionStore.restore(facebook, context);
//			// }
//
//			// SessionEvents.addAuthListener(mSessionListener);
//			// SessionEvents.addLogoutListener(mSessionListener);
//
//		}
//
//		public void login()
//		{
//			// GlobalVariable fbBtn = ((GlobalVariable)
//			// getApplicationContext());
//			// Boolean fbButton = fbBtn.getfbBtn();
//			// if (fbButton == true)
//			// {
//			if (!facebook.isSessionValid())
//			{
//				facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());
//			}
//			// }
//			else
//			{
//				// globalVar = ((GlobalVariable) getApplicationContext());
////				name.setText("Hello " + sharedPref.getString("facebookName", "") + ",");
//				mProgress.dismiss();
//			}
//		}
//
//		private final class LoginDialogListener implements DialogListener {
//			public void onComplete(Bundle values)
//			{
//				SessionEvents.onLoginSuccess();
//
//				mProgress.setMessage("Finalizing ...");
//		        mProgress.show();
//				AsyncFacebookRunner syncRunner = new AsyncFacebookRunner(facebook);
//				syncRunner.request("me", new LoginRequestListener());				
//			}
//
//			public void onFacebookError(FacebookError error)
//			{
//				SessionEvents.onLoginError(error.getMessage());
//			}
//
//			public void onError(DialogError error)
//			{
//				SessionEvents.onLoginError(error.getMessage());
//			}
//
//			public void onCancel()
//			{
//				SessionEvents.onLoginError("Action Canceled");
//				Intent intent = new Intent(context, LoginPage.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//			}
//		}		
//		
//		public class LoginRequestListener extends BaseRequestListener {
//			public void onComplete(String response, final Object state)
//			{
//				try
//				{
//					// process the response here: executed in background thread
//					Log.d("Facebook-Example", "Response: " + response.toString());
//					JSONObject json = Util.parseJson(response);
//					fnameS = json.getString("first_name");
//					lnameS = json.getString("last_name");
//					nameS = fnameS + " " + lnameS;
//					// emailS = json.getString("email");
//					// genderS = json.getString("gender");
//					// bdayS = json.getString("birthday");
//					Log.d("Facebook", fnameS);
//					// userS = new UserParticulars(fnameS, lnameS, emailS,
//					// genderS, bdayS);
//
//					// callback should be run in the original thread,
//					// not the background thread
//					mHandler.post(new Runnable()
//					{
//						public void run()
//						{
//							editor.putString("facebookName", nameS);
//							editor.commit();
////							name.setText("Hello " + nameS + ",");
//							mProgress.dismiss();
//							// globalVar = ((GlobalVariable)
//							// getApplicationContext());
//							// globalVar.setName(nameS);
//
//							// String token = facebook.getAccessToken();
//							// long token_expires = facebook.getAccessExpires();
//
//							// SharedPreferences prefs=
//							// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
//							//
//							// prefs.edit().putLong("access_expires",
//							// token_expires).commit();
//							//
//							// prefs.edit().putString("access_token",
//							// token).commit();
//							//
//							// prefs.edit().putString("name", nameS).commit();
//							// fname.setText(fnameS);
//							// lname.setText(lnameS);
//							// email.setText(emailS);
//							// if (genderS.equals("male"))
//							// {
//							// male.setChecked(true);
//							// female.setChecked(false);
//							// }
//							// else if (genderS.equals("female"))
//							// {
//							// female.setChecked(true);
//							// male.setChecked(false);
//							// }
//							//
//							// for (int i = 0; i < 3; i++)
//							// {
//							// bdayInt[i] = getBday(bdayS)[i];
//							// }
//							// bday.setText("Your Birthdate is: " + bdayInt[0]
//							// +"/" + bdayInt[1] + "/" + bdayInt[2]);
//						}
//					});
//				}
//				catch (JSONException e)
//				{
//					Log.w("Facebook-Example", "JSON Error in response");
//				}
//				catch (FacebookError e)
//				{
//					Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
//				}
//			}
//		}
//
//		public Facebook getFacebook()
//		{
//			return this.facebook;
//		}
//
//	}

	protected AlertDialog onCreateDialog(int id)
	{
		AlertDialog alertDialog;
		// do the work to define the error Dialog
		alertDialog = new AlertDialog.Builder(Search.this).create();
		alertDialog.setTitle("Login Error");

		switch (id)
		{
			case DIALOG_ERR_LOGIN:
				alertDialog.setMessage("Could not authenticate you. Perhaps your details were not saved. Please login again.");
				break;

			default:
				alertDialog = null;
		}
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				// here you can add functions
				dialog.cancel();
				Intent launchLogin = new Intent(Search.this, LoginPage.class);
				startActivity(launchLogin);

			}
		});
		return alertDialog;
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

	private class GPSLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{

				point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));

				// add marker
				// MapOverlay mapOverlay = new MapOverlay(MY_POINT);
				// mapOverlay.setPointToDraw(point);
				// listOfOverlays = mapView.getOverlays();
				// listOfOverlays.clear();
				// listOfOverlays.add(mapOverlay);

				// String address = ConvertPointToLocation(point);
				// Log.d("Address: ", address);

				// Drawable drawable =
				// getResources().getDrawable(R.drawable.red);

				// searchStores(point);

				// mapView.invalidate();
			}
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
}
