package com.ichat.activity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Registration;

import com.ichat.context.MyContext;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePwd extends Activity{
	private EditText password;
	private EditText passwordConfirm;
	private MyContext myContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_pwd);
		myContext = (MyContext) getApplication();
		password=(EditText) findViewById(R.id.password);
		passwordConfirm=(EditText) findViewById(R.id.passwordConfirm);
	}
	public void return_back(View v){
		this.finish();
	}
	public void commitPwd(View v){
		String sPwd=password.getText().toString().trim();
		String sPwdComfirm=passwordConfirm.getText().toString().trim();
		if(TextUtils.isEmpty(sPwd)||TextUtils.isEmpty(sPwdComfirm)){
			Toast.makeText(this, "�����ʽ����ȷ������������", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!sPwd.equals(sPwdComfirm)){
			Toast.makeText(this, "�����������벻һ�£�����������", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			myContext.getConn().getAccountManager().changePassword(sPwd);
		} catch (XMPPException e) {
			Toast.makeText(this, "�����޸�ʧ�ܣ����Ժ�����...", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			this.finish();
			return;
		}
		Toast.makeText(this, "�����޸ĳɹ�", Toast.LENGTH_SHORT).show();
		this.finish();
	}
}
