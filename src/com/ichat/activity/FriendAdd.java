package com.ichat.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ichat.context.MyContext;

public class FriendAdd extends Activity {
	private EditText nickName;
	private TextView user;
	private String userJID;
	private TextView group;
	private MyContext myContext;
	private List<RosterGroup> groupList = new ArrayList<RosterGroup>();
	private int selectedGroup = 0;
	private int selectGroup_requestCode = 3;
	private String friend;
	private String serviceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_add);
		myContext = (MyContext) getApplication();
		nickName = (EditText) findViewById(R.id.nickName_et);
		user = (TextView) findViewById(R.id.user_tv);
		group = (TextView) findViewById(R.id.group_tv);

		friend = (String) getIntent().getExtras().get("friend").toString().trim();
		serviceName = myContext.getConn().getServiceName();
		user.setText(friend + "@" + serviceName);
		// 取得第一个分组
		groupList.addAll(myContext.getRoster().getGroups());
		group.setText(groupList.get(selectedGroup).getName());

	}

	/*
	 * 发送好友添加请求
	 */
	public void sendAddInfo(View v) {
		try {
			myContext.getRoster().createEntry(friend + "@" + serviceName,
					nickName.getText().toString().trim(),
					new String[] { groupList.get(selectedGroup).getName() });
		} catch (XMPPException e) {
			Log.e("error", "添加用户:" + friend + " 为好友请求发送失败...");
			Toast.makeText(this, "请求发送失败...", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		Toast.makeText(this, "请求已发送", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	public void reurnBack(View v) {
		this.finish();
	}

	public void selectGroup(View v) {
		Intent intent = new Intent(this, ChangeGroup.class);
		intent.putExtra("requestCode", selectGroup_requestCode);
		intent.putExtra("groupPosition", selectedGroup);
		startActivityForResult(intent, selectGroup_requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == selectGroup_requestCode) {
			int newGroupNo = data.getExtras().getInt("newGroupNo");
			if (selectedGroup != newGroupNo) {
				group.setText(groupList.get(newGroupNo).getName());
				selectedGroup = newGroupNo;
			} else {

			}
		}
	}
}
