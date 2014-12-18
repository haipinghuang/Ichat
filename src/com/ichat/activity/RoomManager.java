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
		// �ж��Ƿ�֧��Ⱥ��
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
			Log.e("error", "��������ʧ��");
			e.printStackTrace();
			return;
		}
		Form form = null;
		try {
			form = muc.getConfigurationForm();
		} catch (XMPPException e) {
			Log.e("error", "���÷���ʧ��");
			e.printStackTrace();
		}
		Form submitForm = form.createAnswerForm();
		submitForm.setAnswer("muc#roomconfig_persistentroom", true);
		// ������Գ�Ա����
		submitForm.setAnswer("muc#roomconfig_membersonly", false);
		// ����ռ��������������
		submitForm.setAnswer("muc#roomconfig_allowinvites", true);
		// �ܹ�����ռ������ʵ JID �Ľ�ɫ
		submitForm.setAnswer("muc#roomconfig_whois", "anyone");
		// ��¼����Ի�
		submitForm.setAnswer("muc#roomconfig_enablelogging", true);
		// ������ע����ǳƵ�¼
		submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
		if (!roomPwd.equals("")) {
			// �����Ƿ���Ҫ����
			submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
			// ���ý�������
			submitForm.setAnswer("muc#roomconfig_roomsecret", roomPwd);
		} else {
			submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false);
		}
		// ����ʹ�����޸��ǳ�
		submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
		// �����û�ע�᷿��
		submitForm.setAnswer("x-muc#roomconfig_registration", false);
		// ��������ɵı�����Ĭ��ֵ����������������������
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
