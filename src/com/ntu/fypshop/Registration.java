package com.ntu.fypshop;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.facebook.android.AsyncFacebookRunner;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class Registration extends Activity {
	// private int mYear;
	// private int mMonth;
	// private int mDay;

	private static GlobalVariable applicationcontext;
	private UserParticulars userS;
	// private String fnameS;
	// private String lnameS;
	// private String nameS;
	// private String emailS;
	// private String genderS;
	// private String bdayS;

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	private Button regBtn;
	private EditText name;
	// private EditText lname;
	private EditText email;
	private EditText password;
	// private RadioButton male;
	// private RadioButton female;

	private AsyncFacebookRunner mAsyncRunner;
	// Integer[] bdayInt = new Integer[3];

	UserParticulars user;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		// SessionStore.restore(facebook, getApplicationContext());

		// fbConnect.login();
		// facebook = new Facebook(APP_ID);
		// mAsyncRunner = new AsyncFacebookRunner(facebook);
		// fbConnect.init(this, facebook, FACEBOOK_PERMISSION);
		// btn = (Button) findViewById(R.id.bdateBtn);
		// fname = (EditText) findViewById(R.id.fnameTxtBox);
		// lname = (EditText) findViewById(R.id.lnameTxtBox);

		name = (EditText) findViewById(R.id.nameTxtBox);
		email = (EditText) findViewById(R.id.emailTxtBox);
		password = (EditText) findViewById(R.id.passwordTxtBox);
		regBtn = (Button) findViewById(R.id.regBtn);

		reginit();
		// bday = (TextView) findViewById(R.id.bdateTxt1);
		// male = (RadioButton) findViewById(R.id.male);
		// female = (RadioButton) findViewById(R.id.female);
		// Calendar cal = Calendar.getInstance();
		// mYear = cal.get(Calendar.YEAR);
		// mMonth = cal.get(Calendar.MONTH);
		// mDay = cal.get(Calendar.DAY_OF_MONTH);
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
		// btn.setOnClickListener(new OnClickListener()
		// {
		// public void onClick(View v)
		// {
		//
		// showDialog(DATE_DIALOG_ID);
		// }
		// });
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

	public void reginit()
	{
		regBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ConnectDB connect;
				try
				{
					connect = new ConnectDB(name.getText().toString(), email.getText().toString(), password.getText().toString());
					if (connect.inputResult())
					{
						GlobalVariable globalName = ((GlobalVariable) getApplicationContext());
						globalName.setName(name.getText().toString());
						// TODO Auto-generated method stub
						Intent intent = new Intent(v.getContext(), SearchShops.class);
						startActivityForResult(intent, 2);
					}
					else
					{
						// give error etc.
					}
				}
				catch (NoSuchAlgorithmException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
	}
	// final OnDateSetListener odsl = new OnDateSetListener()
	// {
	// public void onDateSet(DatePicker view, int year, int month, int
	// dayOfMonth)
	// {
	// bday.setText("Your Birthdate is: " + dayOfMonth + "/" + (month+1) + "/" +
	// year);
	// }
	// };

	// @Override
	// protected Dialog onCreateDialog(int id)
	// {
	// switch (id)
	// {
	// case DATE_DIALOG_ID:
	// if (bdayInt[0] == 0 || bdayInt[1] == 0 || bdayInt[2] == 0)
	// {
	// return new DatePickerDialog(this, odsl, mYear, mMonth, mDay);
	// }
	// else
	// {
	// return new DatePickerDialog(this, odsl, bdayInt[2], bdayInt[1] - 1,
	// bdayInt[0]);
	// }
	// }
	// return null;
	// }

}
