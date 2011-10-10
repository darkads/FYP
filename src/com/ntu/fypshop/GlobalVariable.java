package com.ntu.fypshop;

import com.facebook.android.Facebook;

import android.app.Application;

public class GlobalVariable extends Application 
{
	private static final String APP_ID = "222592464462347";
      private Facebook myFbState = new Facebook(APP_ID);
      private Boolean fbBtn = false;
      private String name = "";

      public Facebook getFBState()
      {
        return myFbState;
      }//End method

      public void setFbState(Facebook f)
      {
        myFbState = f;
      }//End method
      
      public Boolean getfbBtn()
      {
    	  return fbBtn;
      }
      
      public void setfbBtn(Boolean b)
      {
    	  fbBtn = b;
      }
      
      public String getName()
      {
    	  return name;
      }
      
      public void setName(String n)
      {
    	  name = n;
      }
}//End Class
