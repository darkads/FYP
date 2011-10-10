package com.ntu.fypshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SearchShops extends Activity implements OnClickListener {

	
	private static final String APP_ID = "222592464462347";
	FbConnect fbConnect;

	private TextView name, clothes, others;
	private UserParticulars userS;
	private String fnameS;
	private String lnameS;
	private String nameS;
	private String emailS;
	//private String genderS;
	//private String bdayS;

	//static final int DATE_DIALOG_ID = 0;
	//private TextView bday;
	//private Button btn;
	//private EditText fname;

	//private EditText lname;
	
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
		
		init();
		fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
	}
	
	private void init()
	{
		// TODO Auto-generated method stub
		clothes.setOnClickListener(this);
		others.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// if (requestCode != 0)
		// {
		fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
		// }
	}
	
	
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    // Handle item selection
//	    switch (item.getItemId()) {
//	    case R.id.clothes:
//	        showResults("Clothes");
//	        return true;
//	    case R.id.others:
//	    	showResults("Others");
//	        return true;
//	    default:
//	        return super.onOptionsItemSelected(item);
//	    }
//	}
	public void onClick(View v)
	{
		
		Intent intent = new Intent(this, MapResult.class);
		startActivity(intent);
	}
	
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
			GlobalVariable fbBtn = ((GlobalVariable) getApplicationContext());
			Boolean fbButton = fbBtn.getfbBtn();
			if (fbButton == true)
			{
				if (!facebook.isSessionValid())
				{
					facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());

				}
			}
			else
			{
				GlobalVariable globalName = ((GlobalVariable) getApplicationContext());
				name.setText("Hello " + globalName.getName() + ",");
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
//					genderS = json.getString("gender");
//					bdayS = json.getString("birthday");
					Log.d("Facebook", fnameS);
//					userS = new UserParticulars(fnameS, lnameS, emailS, genderS, bdayS);
					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							name.setText("Hello " + nameS + ",");
							//fname.setText(fnameS);
							//lname.setText(lnameS);
							//email.setText(emailS);
//							if (genderS.equals("male"))
//							{
//								male.setChecked(true);
//								female.setChecked(false);
//							}
//							else if (genderS.equals("female"))
//							{
//								female.setChecked(true);
//								male.setChecked(false);
//							}
//							
//							for (int i = 0; i < 3; i++)
//							{
//								bdayInt[i] = getBday(bdayS)[i];
//							}
//							bday.setText("Your Birthdate is: " + bdayInt[0] +"/" + bdayInt[1] + "/" + bdayInt[2]);
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

//		public Integer[] getBday(String b)
//		{
//			int mth, day, year;
//			Integer[] bday = new Integer[3];
//
//			String birthday = b;
//
//			for (int i = 0; i < 3; i++)
//			{
//				bday[i] = 0;
//			}
//			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//			try
//			{
//				Date date = sdf.parse(birthday);
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(date);
//				day = (cal.get(Calendar.DAY_OF_MONTH));
//				mth = (cal.get(Calendar.MONTH));
//				year = (cal.get(Calendar.YEAR));
//				bday[0] = day;
//				bday[1] = mth + 1;
//				bday[2] = year;
//			}
//			catch (ParseException pe)
//			{
//				Log.i("Parse Date error: ", "The date povided is invalid.");
//			}
//			return bday;
//		}
	}
}
