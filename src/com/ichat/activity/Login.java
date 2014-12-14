package com.ichat.activity;

import java.util.Timer;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ichat.config.MyConfig;
import com.ichat.context.MyContext;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	ConnectionConfiguration config;
	XMPPConnection conn = null;
	private MyContext myContext;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		myContext = (MyContext) getApplication();
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
			boolean network = networkState();
			if (network == false) {
				Toast.makeText(this, "网络连接不可用", Toast.LENGTH_SHORT).show();
				return;
			} else {
				dialog = new ProgressDialog(this);
				dialog.setMessage("登录中...");
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setCancelable(true);
				dialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Looper.prepare();
						login_commit(username, pwd);
						Looper.loop();
						Looper.myLooper().quit();
					}
				}).start();

			}
		}
	}
	private void login_commit(String username, String pwd) {
		config = new ConnectionConfiguration(MyConfig.host, MyConfig.port);
		config.setSASLAuthenticationEnabled(false);
		config.setReconnectionAllowed(true);
		config.setSendPresence(false);
		config.setDebuggerEnabled(true);
		config.setSecurityMode(SecurityMode.disabled);
		conn = (myContext.getConn() == null ? new XMPPConnection(config)
				: myContext.getConn());
		try {
			conn.connect();
		} catch (XMPPException e) {
			dialog.cancel();
			Log.e("error", "连接服务器失败");
			e.printStackTrace();
			new AlertDialog.Builder(Login.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("连接服务器失败").setMessage("请稍后重试！").create().show();
			return;
		}
		try {
			conn.login(username, pwd);
		} catch (XMPPException e) {
			dialog.cancel();
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
		dialog.cancel();
		myContext.setConn(conn);
		myContext.setUserName(mUser.getText().toString().trim());
		Intent intent = new Intent();
		intent.setClass(Login.this, MainChat.class);
		startActivity(intent);
		srartService();
		this.finish();
		Welcome.instance.finish();
	}

	private void srartService() {
		Intent service = new Intent();
		service.setAction("FileReceiverService");
		startService(service);
	}
	private boolean networkState() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		boolean internet = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		if (wifi || internet) {
			return true;
		} else {
			return false;
		}

	}

	public void login_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void login_forget_pwd(View v) { // 忘记密码按钮
		Uri uri = Uri.parse("http://3g.qq.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	/**
	 * 设计一个计时任务，到一定时间没登录成功的话自动取消登录
	 */
	public void test() {
		Timer timer = new Timer();
	}
}
