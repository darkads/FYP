package com.ntu.fypshop;

import com.facebook.android.Facebook;

import android.app.Application;

public class GlobalVariable extends Application 
{
	private static final String APP_ID = "222592464462347";
      private Facebook myFbState = new Facebook(APP_ID);
      private Boolean fbBtn = false;
      private String name = "";
      private String password = "";
      private String email = "";
      private Integer searchType = 0;

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
      
      public String getHashPw()
      {
    	  return password;
      }
      
      public void setHashPw(String p)
      {
    	  password = p;
      }
      
      public String getEm()
      {
    	  return email;
      }
      
      public void setEm(String em)
      {
    	  email = em;
      }
      
      public void setSearchType(Integer st)
      {
    	  searchType = st;
      }
      
      public Integer getSearchType()
      {
    	  return searchType;
      }
}//End Class
