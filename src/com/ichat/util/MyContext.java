package com.ichat.util;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;

import android.app.Application;

public class MyContext extends Application {
	private String userName;
	private XMPPConnection conn = null;
	private List<Chat> chatList = new ArrayList<Chat>();

	public List<Chat> getChatList() {
		return chatList;
	}

	public XMPPConnection getConn() {
		return conn;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Roster getRoster() {
		return conn.getRoster();
	}

	public ChatManager getChatManager() {
		return conn.getChatManager();
	}

	public void setConn(XMPPConnection conn) {
		this.conn = conn;
	}

	@Override
	public void finalize() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getConn().disconnect();
			}
		}).start();
		this.conn.disconnect();
	}

}
