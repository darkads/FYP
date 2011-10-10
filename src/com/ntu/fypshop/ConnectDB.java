package com.ntu.fypshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.Util;

import android.util.Log;

public class ConnectDB {

	private JSONArray jArray;
	private Boolean flag;
	String result = "";
	String salt = "";
	String name = "";
	InputStream is = null;

	public ConnectDB(String email, String password)
	{
		// the data to send
		ArrayList<NameValuePair> checkSalt = new ArrayList<NameValuePair>();
		checkSalt.add(new BasicNameValuePair("email", email));

		// String response = "";
		BufferedReader inSalt = null, in = null;

		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost emailRequest = new HttpPost("http://10.0.2.2/getSalt.php");
			emailRequest.setEntity(new UrlEncodedFormEntity(checkSalt));
			HttpResponse emailResponse = client.execute(emailRequest);
			HttpEntity saltEntity = emailResponse.getEntity();
			inSalt = new BufferedReader(new InputStreamReader(saltEntity.getContent(), "iso-8859-1"), 8);
			String bsalt = inSalt.readLine();
			// JSONTokener tokener = new JSONTokener(bsalt);
			// JSONArray finalResult = new JSONArray(tokener);
			JSONObject jsonObj = new JSONObject(bsalt);
			Log.d("Salt: ", jsonObj.getString("salt"));
			salt = jsonObj.getString("salt");
			name = jsonObj.getString("name");
			Log.d("Name: ", name);
			inSalt.close();

			String hashedPass = new String(getHash(1000, password, salt.getBytes()), "UTF8");
			Log.d("Hashed Password: ", hashedPass);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", hashedPass));

			HttpPost request = new HttpPost("http://10.0.2.2/authenticateUser.php");
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			in = new BufferedReader(new InputStreamReader(entity.getContent()));
			// StringBuffer sb = new StringBuffer("");
			// String line = "";
			// String NL = System.getProperty("line.separator");
			// while ((line = in.readLine()) != null)
			// {
			// sb.append(line + NL);
			// }
			if (in.readLine() == null)
			{
				result = "0";
			}
			else
			{
				result = "1";
			}
			in.close();
			Log.d("Result: ", result);
			// result = sb.toString();

			// result = result.replaceAll("\\s+", "");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public ConnectDB(String name, String email, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{

		// the data to send
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("email", email));
		// Uses a secure Random not a simple Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		random.nextBytes(salt);

		String hashedPass = new String(getHash(1000, password, Base64.encodeBase64(salt)), "UTF8");
		nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
		nameValuePairs.add(new BasicNameValuePair("salt", new String(Base64.encodeBase64(salt), "UTF8")));

		for (int i = 0; i < 4; i++)
		{
			Log.d(Integer.toString(i), nameValuePairs.get(i).getName() + ": " + nameValuePairs.get(i).getValue());
		}

		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://10.0.2.2/insertUser.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}

		catch (Exception ex)
		{
			Log.e("log_tag", "Error in http connection " + ex.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			// StringBuilder sb = new StringBuilder();
			String line = null;
			line = reader.readLine();
			// while ((line = reader.readLine()) != null)
			// {
			// sb.append(line + "\n");
			// }
			is.close();

			result = line;// sb.toString();
			Log.d("Result: ", result);
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}

		// parse json data
		// try
		// {
		// jArray = new JSONArray(result);
		// for (int i = 0; i < jArray.length(); i++)
		// {
		// JSONObject json_data = jArray.getJSONObject(i);
		// //Log.i("log_tag", "id: " + json_data.getInt("id") + ", name: " +
		// json_data.getString("name") + ", sex: " + json_data.getInt("sex") +
		// ", birthyear: " + json_data.getInt("birthyear"));
		// }
		// }
		// catch (JSONException je)
		// {
		// Log.e("log_tag", "Error parsing data " + je.toString());
		// }
	}

	public String getName()
	{
		return name;
	}

	public Boolean inputResult()
	{
		if (result.equals("1"))
		{			
			return true;
		}
		else
		{
			return false;
		}
	}

	public byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++)
		{
			digest.reset();
			input = digest.digest(input);
		}
		return Base64.encodeBase64(input);
	}

}
