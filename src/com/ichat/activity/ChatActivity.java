package com.ichat.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ichat.adaper.ChatMsgViewAdapter;
import com.ichat.adaper.ExpressionAdapter;
import com.ichat.adaper.ExpressionPagerAdapter;
import com.ichat.config.MyConfig;
import com.ichat.context.MyContext;
import com.ichat.dao.ChatDao;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.mode.ChatMsgEntity.Type;
import com.ichat.mode.Entry;
import com.ichat.mode.ImageMsg;
import com.ichat.util.ChatUtil;
import com.ichat.util.Date;
import com.ichat.util.Out;
import com.ichat.util.SmileUtils;
import com.ichat.widget.ExpandGridView;

public class ChatActivity extends Activity implements OnClickListener {
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView; // 聊天内容view
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private Button btnMore;
	public String playMsgId;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private ChatDao chatDao;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ChatMsgViewAdapter mAdapter;
	private TextView userName;
	private TextView presence;
	private ChatMsgEntity chatMsg_send;
	private ChatMsgEntity chatMsg_rec;
	private String content_send;
	private Entry entry;
	private Chat chat = null;
	private String partner;
	private MyMessageListener myMessageListenern = new MyMessageListener();
	private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
	private MyContext myContext;
	private BroadcastReceiver receiver;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initView();
		setUpView();
		initFileTransfer();
	}

	private void initFileTransfer() {
		ServiceDiscoveryManager sdManager = ServiceDiscoveryManager
				.getInstanceFor(myContext.getConn());
		sdManager.addFeature("http://jabber.org/protocol/disco#info");
		sdManager.addFeature("jabber:iq:privacy");
		FileTransferNegotiator.setServiceEnabled(myContext.getConn(), true);
		boolean flag = FileTransferNegotiator.isServiceEnabled(myContext
				.getConn());
		// 可以确定FileTransfer enable；
	}

	private void setUpView() {
		chatDao = new ChatDao(this);
		entry = (Entry) this.getIntent().getExtras().getSerializable("entry");
		List<Chat> chatList = myContext.getChatList();
		for (Chat ch : chatList) {
			if (ChatUtil.getPartnerName(ch).equals(entry.getPartner())) {
				Iterator<MessageListener> it = ch.getListeners().iterator();
				while (it.hasNext()) {
					ch.removeMessageListener((MessageListener) it.next());
				}
				chat = ch;
				break;
			}
		}
		if (chat == null) {
			chat = myContext.getChatManager().createChat(entry.getUserJID(),
					myMessageListenern);
		} else {
			chat.addMessageListener(myMessageListenern);
		}
		myContext.getChatList().add(chat);
		msgList.addAll(chatDao.find(ChatUtil.getPartnerName(chat)));
		if (msgList.size() > 0) {
			listView.setSelection(msgList.size() - 1);
		}
		userName.setText(ChatUtil.getPartnerName(chat));
		partner = ChatUtil.getPartnerName(chat);
		presence.setText(entry.getStatus());

		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	public class MyMessageListener implements MessageListener {
		@Override
		public void processMessage(final Chat chat, Message arg1) {
			MainChat.instance.playMedia();
			chatMsg_rec = new ChatMsgEntity(Date.getDate(), true, true,
					partner, arg1.getBody(), null);
			chatMsg_rec = new ChatMsgEntity(Date.getDate(), arg1.getBody(),
					true, true, partner, null, Type.text);
			sendBroadcast(chatMsg_rec);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!TextUtils.isEmpty(chatMsg_rec.getText())) {
						chatDao.add(chatMsg_rec);
						MainChat.instance.playMedia();
						MainChat.instance
								.sendNotification(chatMsg_rec.getText(),
										chatMsg_rec.getPartner());
					}
					msgList.add(chatMsg_rec);
					listViewRefresh();
				}
			});
		}
	}

	public void initView() {
		myContext = (MyContext) getApplication();
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		userName = (TextView) findViewById(R.id.userName_tv);
		presence = (TextView) findViewById(R.id.presence_tv);

		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		receiver = new myBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyConfig.RECEIVE_IMG_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				filter);
		mAdapter = new ChatMsgViewAdapter(this, msgList);
		listView.setAdapter(mAdapter);
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		// voiceRecorder = new VoiceRecorder(micImageHandler);
		// buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	private void send() {
		content_send = mEditTextContent.getText().toString().trim();
		if (content_send.length() > 0) {
			try {
				chat.sendMessage(content_send);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
			chatMsg_send = new ChatMsgEntity(Date.getDate(), content_send,
					false, true, partner, null, Type.text);
			sendBroadcast(chatMsg_send);
			if (!TextUtils.isEmpty(chatMsg_send.getText())) {
				chatDao.add(chatMsg_send);
			}
			msgList.add(chatMsg_send);
			listViewRefresh();
			mEditTextContent.setText("");
			chatMsg_send = null;
			content_send = null;
		}
	}

	private void listViewRefresh() {
		mAdapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
	}

	private void sendBroadcast(ChatMsgEntity msg) {
		Intent intent = new Intent(MyConfig.SYNC_MSG_ACTION);
		Bundle bundle = new Bundle();
		bundle.putSerializable("msg", msg);
		intent.putExtras(bundle);
		LocalBroadcastManager.getInstance(ChatActivity.this).sendBroadcast(
				intent);
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;
	}

	// 清空历史消息
	public void emptyHistory(View v) {

	}

	public void toGroupDetails(View v) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_take_picture:
			// selectPicFromCamera();// 点击照相图标
			break;
		case R.id.btn_picture:
			selectPicFromLocal(); // 点击图片图标
			break;
		case R.id.btn_location:
			// startActivityForResult(new Intent(this, BaiduMapActivity.class),
			// REQUEST_CODE_MAP);
			break;
		case R.id.iv_emoticons_normal:
			more.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
			break;
		case R.id.iv_emoticons_checked:
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			break;
		case R.id.btn_video:
			// 点击摄像图标
			// Intent intent = new Intent(MainActivity.this,
			// ImageGridActivity.class);
			// startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
			break;
		case R.id.btn_file:
			// selectFileFromLocal();
			break;
		case R.id.btn_voice_call:
			// if (!EMChatManager.getInstance().isConnected())
			// Toast.makeText(this, "尚未连接至服务器，请稍后重试", 0).show();
			// else
			// startActivity(new Intent(MainActivity.this,
			// VoiceCallActivity.class).putExtra("username",
			// toChatUsername).putExtra(
			// "isComingCall", false));
			break;
		}
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {

					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class
									.forName("com.ichat.util.SmileUtils");
							Field field = clz.getField(filename);
							Spannable span = SmileUtils.getSmiledText(
									ChatActivity.this, (String) field.get(null));
							mEditTextContent.append(span);
							// mEditTextContent.setText(expression,
							// mEditTextContent.getSelectionStart(),
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
				break;
			}
		}
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(file.getAbsolutePath());
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {

		File file = new File(filePath);
		FileTransferManager fileManager = new FileTransferManager(
				myContext.getConn());
		// Out.println("userJID:"+entry.getUserJID());
		Out.println("filePath:" + file.getAbsolutePath() + "|" + file.length());
		OutgoingFileTransfer transfer = fileManager
				.createOutgoingFileTransfer(entry.getUserJID() + "/Spark 2.6.3");
		try {
			transfer.sendFile(file, file.getName());
			Out.println(file.getName() + "|开始发送|" + transfer.getFileSize());
			Status status = null;
			// while (!transfer.isDone()) {
			// status = transfer.getStatus();
			// Out.println("status:" + status);
			// Thread.sleep(100);
			// }
		} catch (Exception e) {
			Log.e("error", file.getName() + " 发送出错");
			e.printStackTrace();
			return;
		}
		Out.println("发送成功:" + transfer.getBytesSent());
		chatMsg_send = new ChatMsgEntity(Date.getDate(),
				file.getAbsolutePath(), false, true, partner, null, Type.image);
		msgList.add(chatMsg_send);
		listViewRefresh();
		setResult(RESULT_OK);
	}

	public class myBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ImageMsg imageMsg = (ImageMsg) intent
					.getSerializableExtra("imageMsg");
			if (ImageMsg.Type.init.equals(imageMsg.getType())) {
				chatMsg_rec = new ChatMsgEntity(Date.getDate(),
						imageMsg.getImagePath(), true, true, partner, null,
						Type.image);
				msgList.add(chatMsg_rec);
				listViewRefresh();

				Out.println("receive image1");
			}
			if (ImageMsg.Type.receive_finish.equals(imageMsg.getType())) {
				chatMsg_rec = new ChatMsgEntity(Date.getDate(),
						imageMsg.getImagePath(), true, true, partner, null,
						Type.image);
				msgList.add(chatMsg_rec);
				listViewRefresh();
				sendBroadcast(chatMsg_rec);
				if (!TextUtils.isEmpty(chatMsg_rec.getText())) {
					chatDao.add(chatMsg_rec);
				}
				Out.println("receive image2：" + chatMsg_rec.getText());
			}
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public void finish() {
		// chat.removeMessageListener(myMessageListenern);
		// LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.finish();
		this.onDestroy();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}