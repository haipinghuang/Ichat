package com.ichat.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ichat.adaper.ChatMsgViewAdapter;
import com.ichat.config.MyConfig;
import com.ichat.context.MyContext;
import com.ichat.dao.ChatDao;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.mode.Entry;
import com.ichat.util.ChatUtil;
import com.ichat.util.Date;
import com.ichat.util.Out;

public class ChatActivity extends Activity implements OnClickListener {
	private ChatDao chatDao;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private TextView userName;
	private TextView presence;
	private ChatMsgEntity chatMsg_send;
	private String content_send;
	private Entry entry;
	private Chat chat=null;
	private String partner;
	private ChatMsgEntity chatMsg_rec ;
	private MyMessageListener myMessageListenern=new MyMessageListener();
	private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
	private MyContext myContext;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_view);
		myContext=(MyContext) getApplication();
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
		entry = (Entry) this.getIntent().getExtras().getSerializable("entry");
		List<Chat> chatList=myContext.getChatList();
		for(Chat ch:chatList){
			if(ChatUtil.getPartnerName(ch).equals(entry.getPartner())){
				Iterator<MessageListener> it= ch.getListeners().iterator();
				while(it.hasNext()){
					ch.removeMessageListener((MessageListener) it.next());
				}
				chat=ch;
				System.out.println("my chat exist");
				break;
			}
		}
		if(chat==null){
			chat=myContext.getChatManager().createChat(entry.getUserJID(), myMessageListenern);
		}else{
			chat.addMessageListener(myMessageListenern);
		}
		myContext.getChatList().add(chat);
		msgList.addAll(chatDao.find(ChatUtil.getPartnerName(chat)));
		mAdapter = new ChatMsgViewAdapter(this, msgList);
		mListView.setAdapter(mAdapter);
		userName.setText(ChatUtil.getPartnerName(chat));
		partner=ChatUtil.getPartnerName(chat);
		presence.setText(entry.getStatus());
	}

	public class MyMessageListener implements MessageListener {
		@Override
		public void processMessage(final Chat chat, Message arg1) {
			chatMsg_rec= new ChatMsgEntity(Date.getDate(),
					 true,true,partner,arg1.getBody(),null);
			sendBroadcast(chatMsg_rec);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					chatDao.add(chatMsg_rec);
					msgList.add(chatMsg_rec);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount()-1);
				}
			});
		}
	}

	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		userName = (TextView) findViewById(R.id.userName_tv);
		presence = (TextView) findViewById(R.id.presence_tv);
		chatDao=new ChatDao(this);
	}

	private void send() {
		content_send = mEditTextContent.getText().toString().trim();
		if (content_send.length() > 0) {
			try {
				chat.sendMessage(content_send);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
			chatMsg_send = new ChatMsgEntity( Date.getDate(), false,true,partner, content_send,null);
			sendBroadcast(chatMsg_send);
			chatDao.add(chatMsg_send);
			msgList.add(chatMsg_send);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			chatMsg_send = null;
			content_send = null;
			mListView.setSelection(mListView.getCount() - 1);
		}
	}

	private void sendBroadcast(ChatMsgEntity msg) {
		Intent intent=new Intent(MyConfig.action);
		Bundle bundle=new Bundle();
		bundle.putSerializable("msg", msg);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			this.finish();
		}
	}

	/**
	 * 查看用户信息按钮
	 */
	public void head_xiaohei(View v) {
		Intent intent = new Intent(ChatActivity.this, InfoXiaohei.class);
		startActivity(intent);
	}
	protected void onDestroy() {
		super.onDestroy();
		Out.println("ct__onDestroy");
	}
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Out.println("onStart");
//	}
//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		Out.println("onRestart");
//	}
//	@Override
//	protected void onResume() {
//		super.onResume();
//		Out.println("onResume");
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		Out.println("onPause");
//	}
//	@Override
	protected void onStop() {
		super.onStop();
		Out.println("ct__onStop");
	}

}