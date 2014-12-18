package com.ichat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RoomInfo extends Activity{
	private EditText roomName;
	private EditText roomPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_info);
		roomName=(EditText) findViewById(R.id.roomName);
		roomPwd=(EditText) findViewById(R.id.roomPwd);
	}
	public void returnBack(View v){
		this.finish();
	}
	public void finish(View v){
		String sName=roomName.getText().toString().trim();
		String sPwd=roomPwd.getText().toString().trim();
		if(TextUtils.isEmpty(sName)){
			Toast.makeText(this, "群名称不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent=getIntent();
		intent.putExtra("roomName", sName);
		intent.putExtra("roomPwd", sPwd);
		setResult(2, intent);
		this.finish();
	}
}
