package com.ntu.fypshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Browseplace extends Activity {
        
        protected String[] employees;
        protected Integer[] employeesid;
        JSONArray jArray;
        String result = null;
        InputStream is = null;
        StringBuilder sb=null;
        Button search,addplace;
        ListView list;
        
        protected Cursor cursor;
        protected ListAdapter adapter;
        
        double latitude = 0;
        double longitude = 0;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeselection);
        //getShop();
        //ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employees);
        //ListView employeeList = (ListView) findViewById(R.id.list);
        //employeeList.setAdapter(adapter);
        
        search = (Button) findViewById(R.id.searchButton);
        list = (ListView) findViewById(R.id.list);
        addplace = (Button) findViewById(R.id.addPlace);
               
        Bundle bundle=getIntent().getExtras();
        Uri photoUri = (Uri) bundle.get("pic");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(photoUri, new String[] {
        		MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE
               }, null, null, null);
        String fname = "";
              if (c != null) {
                c.moveToFirst();
                fname = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA) );
                latitude = c.getDouble(c.getColumnIndexOrThrow
        (MediaStore.Images.ImageColumns.LATITUDE));
                longitude = c.getDouble(c.getColumnIndexOrThrow
        (MediaStore.Images.ImageColumns.LONGITUDE));
              }
              getShop("",true);
            
		search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	EditText shopname = (EditText) findViewById(R.id.searchText);
            	//doFileUpload();
            	getShop(shopname.getText().toString(), false);
            }
        });
		
		addplace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(Browseplace.this, Addplace.class);
            	startActivity(i);
            }
        }); 
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		    	Intent intent = new Intent(Browseplace.this, Attraction.class);
		    	//Intent intent = new Intent(this, Attraction.class);
		        intent.putExtra("EMPLOYEE_ID", employeesid[pos]);
		        intent.putExtra("EMPLOYEE_NAME", employees[pos]);
		        Bundle bundle=getIntent().getExtras();
		        Uri pic = (Uri) bundle.get("pic");
		        intent.putExtra("pic", pic);
		        startActivity(intent);
		    }
		}); 
    }
        
    public void getShop(String shopname, boolean start){
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	String path = "";
    	if (start){
    		nameValuePairs.add(new BasicNameValuePair("lat",Double.toString(latitude)));
    		nameValuePairs.add(new BasicNameValuePair("long",Double.toString(longitude)));
    		path = "http://10.0.2.2/coordinates.php";
    		//path = "http://192.168.1.80/FYP/coordinates.php";
    	}else{
    		nameValuePairs.add(new BasicNameValuePair("shop",shopname));
    		path = "http://10.0.2.2/database.php";
    		//path = "http://192.168.1.80/FYP/database.php";
    	}
    	//http post
    	try{
    	     HttpClient httpclient = new DefaultHttpClient();
    	     HttpPost httppost = new HttpPost(path);
    	     
    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	     HttpResponse response = httpclient.execute(httppost);
    	     HttpEntity entity = response.getEntity();
    	     is = entity.getContent();
    	     }catch(Exception e){
    	         Log.e("log_tag", "Error in http connection"+e.toString());
    	    }
    	//convert response to string
    	try{
    	      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
    	       sb = new StringBuilder();
    	       sb.append(reader.readLine() + "\n");
    	       String line="0";
    	       while ((line = reader.readLine()) != null) {
    	                      sb.append(line + "\n");
    	        }
    	        is.close();
    	        result=sb.toString();
    	        }catch(Exception e){
    	              Log.e("log_tag", "Error converting result "+e.toString());
    	        }
    	//paring data
    	int ct_id;
    	String ct_name;
    	try{
    	      jArray = new JSONArray(result);
    	      JSONObject json_data=null;
    	      employees = new String[jArray.length()];
    	      employeesid = new Integer[jArray.length()];
    	      for(int i=0;i<jArray.length();i++){
    	             json_data = jArray.getJSONObject(i);
    	             ct_id=json_data.getInt("id");
    	             ct_name=json_data.getString("name");
    	             employees[i] = ct_name;
    	             employeesid[i] = ct_id;
    	         }
    	        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employees);
    	        ListView employeeList = (ListView) findViewById(R.id.list);
    	        employeeList.setAdapter(adapter);
    	      }
    	      catch(JSONException e1){
    	    	  Toast.makeText(getBaseContext(), "No category Found" ,Toast.LENGTH_LONG).show();
    	      } catch (ParseException e1) {
    				e1.printStackTrace();
    		}
    	}
    
    protected void onListItemClick(ListView parent, View view, int position, long id) {
        Intent intent = new Intent(this, Attraction.class);
        intent.putExtra("EMPLOYEE_ID", employeesid[position]);
        intent.putExtra("EMPLOYEE_NAME", employees[position]);
        Bundle bundle=getIntent().getExtras();
        Bitmap pic = (Bitmap) bundle.get("pic");
        intent.putExtra("pic", pic);
        startActivity(intent);
    }
    
}
