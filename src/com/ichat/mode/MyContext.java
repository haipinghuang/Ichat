package com.ichat.mode;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
public class MyContext {
	private String userName;
	private XMPPConnection conn = null;
	private ChatMsgEntity lastMsg=null;
	private List<Chat> chatList=new ArrayList<Chat>();
	private MyContext() {
	};

	private static MyContext context=null;
	public static MyContext getInstance() {
		if(context==null){
			synchronized(MyContext.class){
				if(context==null){
					context=new MyContext();
				}
			}
		}
		return context;
	}
	
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
	
	public ChatMsgEntity getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(ChatMsgEntity lastMsg) {
		this.lastMsg = lastMsg;
	}

	public Roster getRoster() {
		return conn.getRoster();
	}
	public ChatManager getChatManager(){
		return conn.getChatManager();
	}
	public void setConn(XMPPConnection conn) {
		this.conn = conn;
	}
	@Override
	public void finalize() {
		this.conn.disconnect();
		this.context = null;
	}

}
