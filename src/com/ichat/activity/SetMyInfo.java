package com.ichat.activity;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ichat.context.MyContext;

public class SetMyInfo extends Activity {
	private ImageView head;
	private TextView nickName;
	private TextView userName;
	private TextView email;
	private TextView company;
	private TextView tel;
	private MyContext myContext;
	private VCard vcard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_my_info);
		myContext = (MyContext) getApplication();
		head = (ImageView) findViewById(R.id.head);
		userName = (TextView) findViewById(R.id.userName);
		email = (TextView) findViewById(R.id.email);
		company = (TextView) findViewById(R.id.company);
		nickName = (TextView) findViewById(R.id.nickName);
		tel = (TextView) findViewById(R.id.tel);
		vcard = new VCard();
		initInfo();
	}

	private void initInfo() {
		try {
			vcard.load(myContext.getConn());
		} catch (XMPPException e) {
			Log.e("error", "加载个人信息失败");
			e.printStackTrace();
		}
		userName.setText(myContext.getUserName());
		nickName.setText(vcard.getNickName());
		tel.setText(vcard.getPhoneHome("CELL"));
		email.setText(vcard.getEmailHome());
		company.setText(vcard.getOrganization());
	}

	public void return_back(View v) {
		this.finish();
	}

	public void save(View v) {
		String sNickName = nickName.getText().toString().trim();
		String sEmail = email.getText().toString().trim();
		String sCompany = company.getText().toString().trim();
		String sTel = tel.getText().toString().trim();
		vcard.setNickName(sNickName);
		vcard.setPhoneHome("CELL", sTel);
		vcard.setEmailHome(sEmail);
		vcard.setOrganization(sCompany);
		try {
			vcard.save(myContext.getConn());
		} catch (XMPPException e) {
			Toast.makeText(this, "保存失败,请稍后重试...", Toast.LENGTH_SHORT).show();
			Log.e("error", "个人设置信息保存失败...");
			e.printStackTrace();
			return;
		}
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
		this.finish();
	}
}
