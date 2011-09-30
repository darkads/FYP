package com.ntu.fypshop;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Handler;
import android.view.View;
import android.widget.*;

public class LoginPage extends Activity {

	private static final String APP_ID = "222592464462347";
	// private static final String[] FACEBOOK_PERMISSION =
	// { "user_birthday", "email" };

	// private final Handler mFacebookHandler = new Handler();

	private FacebookBtn fblogin;
	private static GlobalVariable FbState, fbBtn;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private ImageButton regBtn;

	Handler mHandler = new Handler();

	// private UserParticulars user;
	// private String fname;
	// private String lname;
	// private String email;
	// private String gender;
	// private String bday;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// if (APP_ID == null)
		// {
		// Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
		// "specified before running this example: see Example.java");
		// }

		FbState = ((GlobalVariable) getApplicationContext());
		mFacebook = FbState.getFBState();
		if (mFacebook.isSessionValid())
		{
			SessionStore.restore(mFacebook, this);
		}
		else
		{
			mFacebook = new Facebook(APP_ID);
		}
		fblogin = (FacebookBtn) findViewById(R.id.fbLoginBtn);
		regBtn = (ImageButton) findViewById(R.id.registerBtn);

		fblogin.init(this, mFacebook, getApplicationContext());
		fbinit();
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);

		reginit();

	}

	// public void fbinit(final Activity activity, final Facebook fb, final
	// String[] permissions)
	// {
	// //mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	// // fblogin.setBackgroundColor(Color.TRANSPARENT);
	// // fblogin.setAdjustViewBounds(true);
	// // fblogin.setImageResource(fb.isSessionValid() ?
	// R.drawable.logout_button : R.drawable.login_button);
	// // fblogin.drawableStateChanged();
	//
	//
	// fblogin.setOnClickListener(new View.OnClickListener()
	// {
	// public void onClick(View v)
	// {
	// fblogin = new FacebookConnector(APP_ID, activity,
	// getApplicationContext(), permissions);
	// facebookConnector.login();
	// }
	// });
	// }
	public void fbinit()
	{
		fblogin.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (!mFacebook.isSessionValid())
				{
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), Registration.class);
					fbBtn = ((GlobalVariable)getApplicationContext());
					fbBtn.setfbBtn(true);
					startActivityForResult(intent, 1);
				}
				else
				{
					SessionEvents.onLogoutBegin();
					AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
					asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
				}
			}
		});
	}

	public void reginit()
	{
		regBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), Registration.class);

				fbBtn = ((GlobalVariable)getApplicationContext());
				fbBtn.setfbBtn(false);
				// startActivityForResult means that Activity1 can expect info
				// back from Activity2.
				startActivityForResult(intent, 0);
			}
		});
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data)
	// {
	// if (requestCode != 0)
	// {
	// this.fblogin.getFacebook().authorizeCallback(requestCode, resultCode,
	// data);
	// }
	// }

	@Override
	protected void onResume()
	{
		super.onResume();
		FbState = ((GlobalVariable) getApplicationContext());
		mFacebook = FbState.getFBState();
		// updateLoginStatus();
		fbinit();
	}

	// public class LoginRequestListener extends BaseRequestListener {
	// public void onComplete(String response, final Object state)
	// {
	// try
	// {
	// // process the response here: executed in background thread
	// Log.d("Facebook-Example", "Response: " + response.toString());
	// JSONObject json = Util.parseJson(response);
	// fname = json.getString("first_name");
	// lname = json.getString("last_name");
	// email = json.getString("email");
	// gender = json.getString("gender");
	// bday = json.getString("birthday");
	// Log.d("Facebook", fname);
	// user = new UserParticulars(fname, lname, email, gender, bday);
	// // callback should be run in the original thread,
	// // not the background thread
	// LoginPage.this.runOnUiThread(new Runnable()
	// {
	// public void run()
	// {
	// // Intent intent = new Intent(getApplicationContext(),
	// Registration.class);
	// // intent.putExtra("data", user);
	// // startActivityForResult(intent, 1);
	// }
	// });
	// }
	// catch (JSONException e)
	// {
	// Log.w("Facebook-Example", "JSON Error in response");
	// }
	// catch (FacebookError e)
	// {
	// Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	// }
	// }
	// }

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
