package com.ntu.fypshop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class Registration extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;

	private static GlobalVariable applicationcontext;
	private UserParticulars userS;
	private String fnameS;
	private String lnameS;
	private String emailS;
	private String genderS;
	private String bdayS;

	static final int DATE_DIALOG_ID = 0;
	private TextView bday;
	private Button btn;
	private EditText fname;
	private EditText lname;
	private EditText email;
	private RadioButton male;
	private RadioButton female;

	private AsyncFacebookRunner mAsyncRunner;
	Integer[] bdayInt = new Integer[3];

	private static final String APP_ID = "222592464462347";
	FbConnect fbConnect;

	UserParticulars user;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}
		// SessionStore.restore(facebook, getApplicationContext());
		fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
		// fbConnect.login();
		// facebook = new Facebook(APP_ID);
		// mAsyncRunner = new AsyncFacebookRunner(facebook);
		// fbConnect.init(this, facebook, FACEBOOK_PERMISSION);
		btn = (Button) findViewById(R.id.bdateBtn);
		fname = (EditText) findViewById(R.id.fnameTxtBox);
		lname = (EditText) findViewById(R.id.lnameTxtBox);
		email = (EditText) findViewById(R.id.emailTxtBox);
		bday = (TextView) findViewById(R.id.bdateTxt1);
		male = (RadioButton) findViewById(R.id.male);
		female = (RadioButton) findViewById(R.id.female);
		Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		// DatePicker dp = (DatePicker) this.findViewById(R.id.bdatePick);
		// dp.init(mYear, mMonth, mDay, null);
		// Bundle data = getIntent().getExtras();
		// if (data != null)
		// {
		// user = data.getParcelable("data");
		// fname.setText(user.getfName());
		// lname.setText(user.getlName());
		// email.setText(user.getEmail());
		// if (user.getGender().equals("male"))
		// {
		// male.setChecked(true);
		// female.setChecked(false);
		// }
		// else if (user.getGender().equals("female"))
		// {
		// female.setChecked(true);
		// male.setChecked(false);
		// }
		// bday.setText("Your Birthdate is: " + user.getBday()[0] + "/" +
		// user.getBday()[1] + "/" + user.getBday()[2]);
		// }
		btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// updateLoginStatus();
		// Bundle data = getIntent().getExtras();
		// if (data != null)
		// {
		// user = data.getParcelable("data");
		// fname.setText(user.getfName());
		// lname.setText(user.getlName());
		// email.setText(user.getEmail());
		// if (user.getGender().equals("male"))
		// {
		// male.setChecked(true);
		// female.setChecked(false);
		// }
		// else if (user.getGender().equals("female"))
		// {
		// female.setChecked(true);
		// male.setChecked(false);
		// }
		// bday.setText("Your Birthdate is: " + user.getBday()[0] + "/" +
		// user.getBday()[1] + "/" + user.getBday()[2]);
		// }

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// if (requestCode != 0)
		// {
		fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
		// }
	}

	final OnDateSetListener odsl = new OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
		{
			bday.setText("Your Birthdate is: " + dayOfMonth + "/" + (month+1) + "/" + year);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case DATE_DIALOG_ID:
				if (bdayInt[0] == 0 || bdayInt[1] == 0 || bdayInt[2] == 0)
				{
					return new DatePickerDialog(this, odsl, mYear, mMonth, mDay);
				}
				else
				{
					return new DatePickerDialog(this, odsl, bdayInt[2], bdayInt[1] - 1, bdayInt[0]);
				}
		}
		return null;
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
					emailS = json.getString("email");
					genderS = json.getString("gender");
					bdayS = json.getString("birthday");
					Log.d("Facebook", fnameS);
					userS = new UserParticulars(fnameS, lnameS, emailS, genderS, bdayS);
					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							// Intent intent = new
							// Intent(getApplicationContext(),
							// Registration.class);
							// intent.putExtra("data", user);
							// startActivityForResult(intent, 1);
							fname.setText(fnameS);
							lname.setText(lnameS);
							email.setText(emailS);
							if (genderS.equals("male"))
							{
								male.setChecked(true);
								female.setChecked(false);
							}
							else if (genderS.equals("female"))
							{
								female.setChecked(true);
								male.setChecked(false);
							}
							
							for (int i = 0; i < 3; i++)
							{
								bdayInt[i] = getBday(bdayS)[i];
							}
							bday.setText("Your Birthdate is: " + bdayInt[0] +"/" + bdayInt[1] + "/" + bdayInt[2]);
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

		public Integer[] getBday(String b)
		{
			int mth, day, year;
			Integer[] bday = new Integer[3];

			String birthday = b;

			for (int i = 0; i < 3; i++)
			{
				bday[i] = 0;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			try
			{
				Date date = sdf.parse(birthday);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				day = (cal.get(Calendar.DAY_OF_MONTH));
				mth = (cal.get(Calendar.MONTH));
				year = (cal.get(Calendar.YEAR));
				bday[0] = day;
				bday[1] = mth + 1;
				bday[2] = year;
			}
			catch (ParseException pe)
			{
				Log.i("Parse Date error: ", "The date povided is invalid.");
			}
			return bday;
		}
	}

}
