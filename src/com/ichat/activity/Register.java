package com.ichat.activity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import com.ichat.config.MyConfig;
import com.ichat.context.MyContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	private EditText userName;
	private EditText pwd;
	private EditText pwd_confirm;
	private MyContext myContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		myContext = (MyContext) getApplication();
		userName = (EditText) findViewById(R.id.userName);
		pwd = (EditText) findViewById(R.id.passwd);
		pwd_confirm = (EditText) findViewById(R.id.passwd_confirm);
	}

	public void register(View v) {
		final String sName = userName.getText().toString().trim();
		final String sPwd = pwd.getText().toString().trim();
		String sPwd_confirm = pwd_confirm.getText().toString().trim();
		if (TextUtils.isEmpty(sName) || TextUtils.isEmpty(sPwd)
				|| TextUtils.isEmpty(sPwd_confirm)
				|| (!sPwd.equals(sPwd_confirm))) {
			showDialog("错误提示", "用户名或密码错误");
			return;
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					register(sName, sPwd);
					Looper.loop();
					Looper.myLooper().quit();
				}
			}).start();

		}
		
	}

	private void register(final String sName, final String sPwd) {

		ConnectionConfiguration config = new ConnectionConfiguration(
				MyConfig.host, MyConfig.port);
		XMPPConnection conn = (myContext.getConn() == null ? new XMPPConnection(
				config) : myContext.getConn());
		try {
			conn.connect();
		} catch (XMPPException e) {
			Log.e("error", "连接服务器失败");
			e.printStackTrace();
			showDialog("连接服务器失败", "请稍后重试！");
			return;
		}
		myContext.setConn(conn);
		AccountManager manager = new AccountManager(conn);
		try {
			manager.createAccount(sName, sPwd);
		} catch (XMPPException e) {
			Log.e("error", "注册失败");
			showDialog("用户名已被注册", "请换一个用户名");
			return;
		}
		Toast.makeText(Register.this, "恭喜你注册成功", Toast.LENGTH_LONG).show();
		Register.this.finish();
	}

	public void showDialog(final String title, final String msg) {
		new AlertDialog.Builder(Register.this)
				.setIcon(
						getResources().getDrawable(R.drawable.login_error_icon))
				.setTitle(title).setMessage(msg).create().show();
	}

	public void login_back(View v) {
		this.finish();
	}
}
