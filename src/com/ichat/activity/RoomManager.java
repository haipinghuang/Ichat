package com.ichat.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ichat.adaper.ChatRoomAdapter;
import com.ichat.context.MyContext;

public class RoomManager extends Activity {
	private ListView room_lv;
	private MyContext myContext;
	private List<String> rooms = null;
	private ChatRoomAdapter chatRoomAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_manager);
		myContext = (MyContext) getApplication();
		// 判断是否支持群聊
		room_lv = (ListView) findViewById(R.id.room_lv);
		rooms = getJoinedRooms(myContext.getConn(), myContext.getConn()
				.getUser());
		chatRoomAdapter = new ChatRoomAdapter(this, rooms);
		room_lv.setAdapter(chatRoomAdapter);
	}


	private void createChatRoom(String name, String roomPwd) {
		MultiUserChat muc = new MultiUserChat(myContext.getConn(), name+"conference.wechat.com");
		try {
			muc.create(myContext.getUserName());
//			muc.join(myContext.getUserName());
		} catch (Exception e) {
			Log.e("error", "创建房间失败");
			e.printStackTrace();
			return;
		}
		Form form = null;
		try {
			form = muc.getConfigurationForm();
		} catch (XMPPException e) {
			Log.e("error", "配置房间失败");
			e.printStackTrace();
		}
		Form submitForm = form.createAnswerForm();
		submitForm.setAnswer("muc#roomconfig_persistentroom", true);
		// 房间仅对成员开放
		submitForm.setAnswer("muc#roomconfig_membersonly", false);
		// 允许占有者邀请其他人
		submitForm.setAnswer("muc#roomconfig_allowinvites", true);
		// 能够发现占有者真实 JID 的角色
		submitForm.setAnswer("muc#roomconfig_whois", "anyone");
		// 登录房间对话
		submitForm.setAnswer("muc#roomconfig_enablelogging", true);
		// 仅允许注册的昵称登录
		submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
		if (!roomPwd.equals("")) {
			// 进入是否需要密码
			submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
			// 设置进入密码
			submitForm.setAnswer("muc#roomconfig_roomsecret", roomPwd);
		} else {
			submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false);
		}
		// 允许使用者修改昵称
		submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
		// 允许用户注册房间
		submitForm.setAnswer("x-muc#roomconfig_registration", false);
		// 发送已完成的表单（有默认值）到服务器来配置聊天室
		try {
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	private List<String> getJoinedRooms(XMPPConnection conn, String user) {
		List<String> rooms = new ArrayList<String>();
		Iterator<String> it = MultiUserChat.getJoinedRooms(conn, user);
		while (it.hasNext()) {
			String room = it.next();
			rooms.add(room);
		}
		return rooms;
	}

	public void roomAdd(View v) {
		Intent intent = new Intent(this, RoomInfo.class);
		startActivityForResult(intent, 2);
	}

	public void finish(View v) {
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 2) {
			String roomName = data.getExtras().getString("roomName");
			String roomPwd = data.getExtras().getString("roomPwd");
			createChatRoom(roomName, roomPwd);
			roomReflash();
		}
	}

	private void roomReflash() {
		rooms.clear();
		rooms = getJoinedRooms(myContext.getConn(), myContext.getConn()
				.getUser());
		chatRoomAdapter.notifyDataSetChanged();

	}

}
