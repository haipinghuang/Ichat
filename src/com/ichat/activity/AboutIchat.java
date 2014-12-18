package com.ichat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AboutIchat extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_ichat);
	}
	public void return_back(View v){
		this.finish();
	}
}
