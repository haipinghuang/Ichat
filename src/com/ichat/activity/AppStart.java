package com.ichat.activity;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent intent = new Intent (AppStart.this,Welcome.class);			
				startActivity(intent);			
				AppStart.this.finish();
			}
		}, 1);
	}
}
