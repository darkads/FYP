package com.ntu.fypshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.ntu.fypshop.LoginPage.LogoutRequestListener;

import android.app.Activity;
import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SearchShops extends Activity {

	private static final String APP_ID = "222592464462347";
	FbConnect fbConnect;

	private static GlobalVariable globalVar;
	private TextView name, clothes, others;
	private Button logout;
	// private UserParticulars userS;
	private String fnameS;
	private String lnameS;
	private String nameS;
	private String emailS;
	private Boolean fbBtn;
	
	Handler mHandler = new Handler();

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1;

	// private String genderS;
	// private String bdayS;

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	// private EditText lname;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search);

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}

		name = (TextView) findViewById(R.id.helloTxt);
		clothes = (TextView) findViewById(R.id.clothes);
		others = (TextView) findViewById(R.id.others);
		logout = (Button) findViewById(R.id.logoutBtn);

		globalVar = ((GlobalVariable) getApplicationContext());
		fbBtn = globalVar.getfbBtn();
		Log.d("FbButton: ", fbBtn.toString());
		if (!fbBtn)
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
					name.setText("Hello " + connectCheck.getName() + ",");
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
		else
		{
			fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
			init(INIT_FB);
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

	private void init(final int type)
	{
		// TODO Auto-generated method stub
		clothes.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), MapResult.class);
				startActivity(intent);
			}
		});
		others.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), MapResult.class);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// if (requestCode != 0)
		// {
		fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
		// }
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

	public class FbConnect {

		private final String[] FACEBOOK_PERMISSION =
		{ "user_birthday", "email" };

		private Context context;
		private Activity activity;
		private Handler mHandler;
		private Facebook facebook;
		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());

		// private SessionListener mSessionListener = new SessionListener();

		public FbConnect(String appId, Activity activity, Context context)
		{

			this.context = context;
			this.mHandler = new Handler();
			this.activity = activity;

			facebook = FbState.getFBState();
			// if (!facebook.isSessionValid())
			// {
			// facebook = new Facebook(APP_ID);
			// FbState.setFbState(facebook);
			login();
			// }
			// else
			// {
			// SessionStore.restore(facebook, context);
			// }

			// SessionEvents.addAuthListener(mSessionListener);
			// SessionEvents.addLogoutListener(mSessionListener);

		}

		public void login()
		{
			// GlobalVariable fbBtn = ((GlobalVariable)
			// getApplicationContext());
			// Boolean fbButton = fbBtn.getfbBtn();
			// if (fbButton == true)
			// {
			if (!facebook.isSessionValid())
			{
				facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());
			}
			// }
			else
			{
				globalVar = ((GlobalVariable) getApplicationContext());
				name.setText("Hello " + globalVar.getName() + ",");
			}
		}

		private final class LoginDialogListener implements DialogListener {
			public void onComplete(Bundle values)
			{
				SessionEvents.onLoginSuccess();
				AsyncFacebookRunner syncRunner = new AsyncFacebookRunner(facebook);
				syncRunner.request("me", new LoginRequestListener());
			}

			public void onFacebookError(FacebookError error)
			{
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onError(DialogError error)
			{
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onCancel()
			{
				SessionEvents.onLoginError("Action Canceled");
			}
		}

		public class LoginRequestListener extends BaseRequestListener {
			public void onComplete(String response, final Object state)
			{
				try
				{
					// process the response here: executed in background thread
					Log.d("Facebook-Example", "Response: " + response.toString());
					JSONObject json = Util.parseJson(response);
					fnameS = json.getString("first_name");
					lnameS = json.getString("last_name");
					nameS = fnameS + " " + lnameS;
					emailS = json.getString("email");
					// genderS = json.getString("gender");
					// bdayS = json.getString("birthday");
					Log.d("Facebook", fnameS);
					// userS = new UserParticulars(fnameS, lnameS, emailS,
					// genderS, bdayS);
					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							name.setText("Hello " + nameS + ",");
							globalVar = ((GlobalVariable) getApplicationContext());
							globalVar.setName(nameS);
							// fname.setText(fnameS);
							// lname.setText(lnameS);
							// email.setText(emailS);
							// if (genderS.equals("male"))
							// {
							// male.setChecked(true);
							// female.setChecked(false);
							// }
							// else if (genderS.equals("female"))
							// {
							// female.setChecked(true);
							// male.setChecked(false);
							// }
							//
							// for (int i = 0; i < 3; i++)
							// {
							// bdayInt[i] = getBday(bdayS)[i];
							// }
							// bday.setText("Your Birthdate is: " + bdayInt[0]
							// +"/" + bdayInt[1] + "/" + bdayInt[2]);
						}
					});
				}
				catch (JSONException e)
				{
					Log.w("Facebook-Example", "JSON Error in response");
				}
				catch (FacebookError e)
				{
					Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
				}
			}
		}

		public Facebook getFacebook()
		{
			return this.facebook;
		}

	}

	protected AlertDialog onCreateDialog(int id)
	{
		AlertDialog alertDialog;
		// do the work to define the error Dialog
		alertDialog = new AlertDialog.Builder(SearchShops.this).create();
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
				Intent launchLogin = new Intent(SearchShops.this, LoginPage.class);
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
}
