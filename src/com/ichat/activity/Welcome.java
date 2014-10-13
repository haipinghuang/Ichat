package com.ichat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class Welcome extends Activity {
	public static Welcome instance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        instance=this;
    }
    public void welcome_login(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this,Login.class);
		startActivity(intent);
      }  
    public void welcome_register(View v) {  
      	Intent intent = new Intent();
//		intent.setClass(Welcome.this,MainWeixin.class);
		startActivity(intent);
      }  
   
}
