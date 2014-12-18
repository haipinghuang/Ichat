package com.ichat.activity;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ichat.context.MyContext;

public class FriendInfo extends Activity{
	private ImageView head;
	private TextView nickName;
	private TextView userName;
	private TextView email;
	private TextView userJID;
	private TextView company;
	private TextView tel;
	private String emptyProperty="暂无数据";
	private MyContext myContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info);
		myContext=(MyContext) getApplication();
		head=(ImageView) findViewById(R.id.head);
		userName=(TextView) findViewById(R.id.userName);
		email=(TextView) findViewById(R.id.email);
		userJID=(TextView) findViewById(R.id.userJID);
		company=(TextView) findViewById(R.id.company);
		nickName=(TextView) findViewById(R.id.nickName);
		tel=(TextView) findViewById(R.id.tel);
		String userJID=getIntent().getExtras().getString("user");
		showInfo(userJID);
	}
	private void showInfo(String userJID2) {
		VCard vcard =new VCard();
		try {
			//userJID==null表示这是要显示本机登录用户的信息。
			if("".equals(userJID2)){  
				vcard.load(myContext.getConn());
			}else{
				vcard.load(myContext.getConn(), userJID2);
			}
		} catch (XMPPException e) {
			Log.e("error", "vcard信息加载失败");
			Toast.makeText(this, "vcard信息加载失败，请稍后重试...", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		if("".equals(userJID2)){
			userName.setText(myContext.getUserName());
			userJID.setText(myContext.getConn().getUser());
		}else{
			RosterEntry entry=myContext.getRoster().getEntry(userJID2);
			userName.setText(entry.getName());
			userJID.setText(entry.getUser());
		}
		if(TextUtils.isEmpty(vcard.getOrganization())){
			company.setText(emptyProperty);
		}else{
			company.setText(vcard.getOrganization());
		}
		if(TextUtils.isEmpty(vcard.getEmailHome())&&TextUtils.isEmpty(vcard.getEmailWork())){
			email.setText(emptyProperty);
		}else{
			email.setText(!vcard.getEmailHome().equals("")?vcard.getEmailHome():vcard.getEmailWork());
		}
		if(TextUtils.isEmpty(vcard.getNickName())){
			nickName.setText(emptyProperty);
		}else{
			nickName.setText(vcard.getNickName());
		}
		if(TextUtils.isEmpty(vcard.getPhoneHome("CELL"))){
			tel.setText(emptyProperty);
		}else{
			tel.setText(vcard.getPhoneHome("CELL"));
		}
	}
	public void return_back(View v){
		this.finish();
	}
}
