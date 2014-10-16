package com.ichat.activity;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.pubsub.PresenceState;

import com.ichat.config.MyConfig;
import com.ichat.mode.MyContext;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	ConnectionConfiguration config;
	XMPPConnection conn=null;
	MyContext context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		init();
	}

	private void init() {
		mUser = (EditText) findViewById(R.id.login_user_edit);
		mPassword = (EditText) findViewById(R.id.login_passwd_edit);
	}

	public void login_mainweixin(View v) {
		final String username = mUser.getText().toString().trim();
		final String pwd = mPassword.getText().toString().trim();
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
			new AlertDialog.Builder(Login.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("登录错误").setMessage("帐号或者密码不能为空").create().show();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					login_commit(username, pwd);
					Looper.loop();
				}
			}).start();
		}
	}

	private void login_commit(String username, String pwd) {
		config = new ConnectionConfiguration(MyConfig.host, MyConfig.port);
		config.setSASLAuthenticationEnabled(false);
		config.setReconnectionAllowed(true);
		config.setSendPresence(false);
		conn=(MyContext.getInstance().getConn()==null?new XMPPConnection(config):MyContext.getInstance().getConn());
		try {
			conn.connect();
		} catch (XMPPException e) {
			Log.e("error", "连接服务器失败");
			e.printStackTrace();
			new AlertDialog.Builder(Login.this)
			.setIcon(
					getResources().getDrawable(
							R.drawable.login_error_icon))
			.setTitle("连接服务器失败").setMessage("请稍后重试！")
			.create().show();
			return;
		}
		try {
			conn.login(username, pwd);
		} catch (XMPPException e) {
			Log.e("error", "登录失败");
			e.printStackTrace();
			new AlertDialog.Builder(Login.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("登录失败").setMessage("帐号或者密码不正确，\n请检查后重新输入！")
					.create().show();
			return;
		}
		MyContext.getInstance().setConn(conn);
		MyContext.getInstance().setUserName(mUser.getText().toString().trim());
		Intent intent=new Intent();
		intent.setClass(Login.this,MainChat.class);
		startActivity(intent);
		this.finish();
		Welcome.instance.finish();
	}

	public void login_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void login_forget_pwd(View v) { // 忘记密码按钮
		Uri uri = Uri.parse("http://3g.qq.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
