package com.ichat.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ichat.adaper.ExpandListViewAdapter;
import com.ichat.adaper.SessionsAdapter;
import com.ichat.dao.ChatDao;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.mode.Entry;
import com.ichat.mode.MyContext;
import com.ichat.mode.Session;
import com.ichat.util.ChatUtil;
import com.ichat.util.Date;
import com.ichat.util.Out;
import com.ichat.util.UserRoster;

/**
 * 登录成功后的主界面
 * 
 * @author huanghai
 */
public class MainChat extends Activity implements OnClickListener {
	public static MainChat instance = null;
	private ViewPager mTabPager;
	private ImageView mTabImg;// 动画图片
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private View view1, view2, view3, view4;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;// 单个水平动画位移
	private int two;
	private int three;
	private LinearLayout mClose;
	private LinearLayout mCloseBtn;
	private View layout;
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	private ArrayList<View> views;
	private PagerAdapter mPagerAdapter;
	private ListView session_lv;
	private List<Session> sessionList = new ArrayList<Session>();
	private SessionsAdapter sessionsAdapter;
	private Bundle bundle;
	private Session session;
	private ChatMsgEntity lastMsg = null;
	private ChatDao chatDao = new ChatDao(this);
	private List<RosterGroup> groupData = new ArrayList<RosterGroup>();
	private List<RosterEntry> childData = new ArrayList<RosterEntry>();
	private ExpandableListView expandListView;
	private ExpandListViewAdapter expandListAdapter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			sessionsAdapter.notifyDataSetChanged();
			session_lv.setSelection(session_lv.getCount() - 1);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_chat);
		// 启动activity时不自动弹出软键盘
		init();
		addChatListener();
		groupData.addAll(MyContext.getInstance().getRoster().getGroups());
		expandListAdapter = new ExpandListViewAdapter(this, groupData);
		expandListView = (ExpandableListView) view2.findViewById(R.id.elv);
		expandListView.setAdapter(expandListAdapter);
		expandListView.setOnChildClickListener(new MyOnChildClickListener());
		mTabPageSetAdapter(views);
		sessionsAdapter = new SessionsAdapter(this, sessionList);
		session_lv = (ListView) view1.findViewById(R.id.session_lv);
		session_lv.setAdapter(sessionsAdapter);
		session_lv.setOnItemClickListener(new SessionItemClickListener());
		receiveMsg();
		receiveOffLineMsg();
		Out.println("M-oncreate");
	}

	private void receiveOffLineMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				OfflineMessageManager offLineMsgManage = new OfflineMessageManager(
						MyContext.getInstance().getConn());
				Iterator<Message> it = null;
				Message msg = null;
				try {
					it = offLineMsgManage.getMessages();
				} catch (XMPPException e) {
					Log.e("MsgError", "获取离线信息失败");
					e.printStackTrace();
				}
				// while (it.hasNext()) {
				// msg = it.next();
				// String msgFrom = msg.getFrom();
				// String msgContent = msg.getBody();
				// Message.Type msgType = msg.getType();
				// Out.println("form:" + msgFrom + "|content:" + msgContent
				// + "|type:" + msgType);
				// }
				try {
					offLineMsgManage.deleteMessages();
				} catch (XMPPException e) {
					Log.e("MsgError", "删除已获取的离线信息失败");
					e.printStackTrace();
				}
				Presence presences = new Presence(Presence.Type.available);
				MyContext.getInstance().getConn().sendPacket(presences);
			}
		}).start();

	}

	private class MyOnChildClickListener implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			childData.addAll(groupData.get(groupPosition).getEntries());
			RosterEntry rosterEntry = childData.get(childPosition);
			Intent intent = new Intent();
			bundle = new Bundle();
			intent.setClass(MainChat.this, ChatActivity.class);
			Entry entry = new Entry(rosterEntry.getName(),
					UserRoster.getPresence(rosterEntry), rosterEntry.getUser());
			bundle.putSerializable("entry", entry);
			intent.putExtras(bundle);
			startActivity(intent);
			childData.clear();
			return false;
		}
	}

	/*
	 * 单独开启一个接收消息更新到会话的线程
	 */
	private void receiveMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					lastMsg = MyContext.getInstance().getLastMsg();
					if (lastMsg != null) {
						String name = (lastMsg.getPartner() != null ? lastMsg
								.getPartner() : lastMsg.getGroupName());
						for (Session s : sessionList) {
							if (s.getName().equals(
									lastMsg.getPartner() != null ? lastMsg
											.getPartner() : lastMsg
											.getGroupName())) {
								sessionList.remove(s);
								break;
							}
						}
						session = new Session(name, lastMsg.getText(), lastMsg
								.getDate());
						sessionList.add(session);
						MyContext.getInstance().setLastMsg(null);
						handler.sendMessage(new android.os.Message());
					}
				}
			}
		}).start();
	}

	private void mTabPageSetAdapter(ArrayList<View> views2) {
		mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		mTabPager.setAdapter(mPagerAdapter);
	}

	/**
	 * 创建chat监听器
	 */
	private void addChatListener() {
		MyContext.getInstance().getChatManager()
				.addChatListener(new ChatManagerListener() {

					@Override
					public void chatCreated(Chat chat, boolean createdLocally) {

						Out.println("main thread " + chat.getThreadID());
						/**
						 * 判断Chat是否由用户主动创建
						 */
						if (!createdLocally) {
							MyContext.getInstance().getChatList().add(chat);
							chat.addMessageListener(new MessageListener() {
								@Override
								public void processMessage(final Chat chat,
										final Message message) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ChatMsgEntity recMsg = new ChatMsgEntity(
													Date.getDate(),
													true,
													false,
													ChatUtil.getPartnerName(chat),
													message.getBody(), null);
											chatDao.add(recMsg);
											session = new Session(ChatUtil
													.getPartnerName(chat),
													message.getBody(), Date
															.getDate());
											/*
											 * 更新会话
											 */
											for (Session s : sessionList) {
												if (s.getName()
														.equals(ChatUtil
																.getPartnerName(chat))) {
													sessionList.remove(s);
												}
											}
											sessionList.add(session);
											sessionsAdapter
													.notifyDataSetChanged();
											session_lv.setSelection(session_lv
													.getCount() - 1);
										}
									});
								}
							});
						} else {
							Out.println("--createdLocally---");
						}
					}
				});
	}

	public class SessionItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			bundle = new Bundle();
			intent.setClass(MainChat.this, ChatActivity.class);
			RosterEntry tEntry = null;
			for (RosterEntry entry : MyContext.getInstance().getRoster()
					.getEntries()) {
				if (entry.getName().equals(sessionList.get(position).getName())) {
					tEntry = entry;
					break;
				}
			}
			Entry entry = new Entry(tEntry.getName(),
					UserRoster.getPresence(tEntry), tEntry.getUser());
			bundle.putSerializable("entry", entry);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private void init() {
		instance = this;
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));
		mTab4.setOnClickListener(new MyOnClickListener(3));
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Display currDisplay = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		int displayWidth = currDisplay.getWidth();
		one = displayWidth / 4; // 设置水平动画平移大小
		two = one * 2;
		three = one * 3;
		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.main_tab_session, null);
		view2 = mLi.inflate(R.layout.main_tab_contact, null);
		view3 = mLi.inflate(R.layout.main_tab_friends, null);
		view4 = mLi.inflate(R.layout.main_tab_settings, null);
		// 每个页面的view数据
		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};

	/*
	 * 页卡切换监听(原作者:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
																				// back键

			if (menu_display) { // 如果 Menu已经打开 ，先关闭Menu
				menuWindow.dismiss();
				menu_display = false;
			} else {
				Intent intent = new Intent();
				intent.setClass(MainChat.this, Exit.class);
				startActivity(intent);
			}
		}

		else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
			if (!menu_display) {
				// 获取LayoutInflater实例
				inflater = (LayoutInflater) this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				// 这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
				// 该方法返回的是一个View的对象，是布局中的根
				layout = inflater.inflate(R.layout.main_menu, null);

				// 下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单
				menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT); // 后两个参数是width和height
				// menuWindow.showAsDropDown(layout); //设置弹出效果
				// menuWindow.showAsDropDown(null, 0, layout.getHeight());
				menuWindow.showAtLocation(this.findViewById(R.id.mainchat),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				// 如何获取我们main中的控件呢？也很简单
				mClose = (LinearLayout) layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout) layout
						.findViewById(R.id.menu_close_btn);

				// 下面对每一个Layout进行单击事件的注册吧。。。
				// 比如单击某个MenuItem的时候，他的背景色改变
				// 事先准备好一些背景图片或者颜色
				mCloseBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(MainChat.this, Exit.class);
						startActivity(intent);
						menuWindow.dismiss(); // 响应点击事件之后关闭Menu
					}
				});
				menu_display = true;
			} else {
				// 如果当前已经为显示状态，则隐藏起来
				menuWindow.dismiss();
				menu_display = false;
			}
			return false;
		}
		return false;
	}
	// 设置标题栏右侧按钮的作用
	public void btnmainright(View v) {
		Intent intent = new Intent(MainChat.this, MainTopRightDialog.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_addFriend:
			Out.println("R.id.add_friend");
			break;
		case R.id.btn_shake:// 手机摇一摇
			intent = new Intent(MainChat.this, ShakeActivity.class);
			startActivity(intent);
			break;
		case R.id.exit_settings:
			intent = new Intent(MainChat.this, ExitFromSettings.class);
			startActivity(intent);
			break;
		default:

		}

	}
}
