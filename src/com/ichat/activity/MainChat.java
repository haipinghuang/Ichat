package com.ichat.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
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
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.ichat.adaper.ExpandListViewAdapter;
import com.ichat.adaper.SessionsAdapter;
import com.ichat.config.MyConfig;
import com.ichat.dao.ChatDao;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.mode.Entry;
import com.ichat.mode.Session;
import com.ichat.util.ChatUtil;
import com.ichat.util.Date;
import com.ichat.util.MyContext;
import com.ichat.util.Out;

/**
 * 登录成功后的主界面
 * 
 * @author huanghai
 */
public class MainChat extends Activity {
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
	private ChatDao chatDao = new ChatDao(this);
	private List<RosterGroup> groupData = new ArrayList<RosterGroup>();
	private List<RosterEntry> childData = new ArrayList<RosterEntry>();
	private ExpandableListView expandListView;
	private ExpandListViewAdapter expandListAdapter;
	private MyBroadcastReceiver myReceiver = new MyBroadcastReceiver();
	private MyContext myContext;
	/**
	 * 监听回话变化
	 */
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
		groupData.addAll(myContext.getRoster().getGroups());
		expandListAdapter = new ExpandListViewAdapter(this, groupData);
		expandListView = (ExpandableListView) view2.findViewById(R.id.elv);
		expandListView.setAdapter(expandListAdapter);
		expandListView.setOnChildClickListener(new MyOnChildClickListener());
		expandListView.setLongClickable(true);
		expandListView
				.setOnItemLongClickListener(new MyOnItemLongClickListener());

		mTabPageSetAdapter(views);
		sessionsAdapter = new SessionsAdapter(this, sessionList);
		session_lv = (ListView) view1.findViewById(R.id.session_lv);
		session_lv.setAdapter(sessionsAdapter);
		session_lv.setOnItemClickListener(new SessionItemClickListener());

		IntentFilter filter = new IntentFilter(MyConfig.action);
		registerReceiver(myReceiver, filter);

		receiveOffLineMsg();
		addRosterListerer();
	}

	// 监听好友变动
	private void addRosterListerer() {
		myContext.getRoster().addRosterListener(new RosterListener() {

			@Override
			public void presenceChanged(Presence arg0) {
				expandListDataChanged();
			}

			@Override
			public void entriesUpdated(Collection<String> arg0) {
				expandListDataChanged();
			}

			@Override
			public void entriesDeleted(Collection<String> arg0) {
				expandListDataChanged();
				Out.println("entriesDeleted");
			}

			@Override
			public void entriesAdded(Collection<String> arg0) {
				expandListDataChanged();
			}
		});
	}

	private void expandListDataChanged() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// myContext.getRoster().reload();
				groupData.clear();
				groupData.addAll(myContext.getRoster().getGroups());
				expandListAdapter.notifyDataSetChanged();
			}
		});

	}

	private void receiveOffLineMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				OfflineMessageManager offLineMsgManage = new OfflineMessageManager(
						myContext.getConn());
				try {
					offLineMsgManage.getMessages();
				} catch (XMPPException e) {
					Log.e("MsgError", "获取离线信息失败");
					e.printStackTrace();
				}
				try {
					offLineMsgManage.deleteMessages();
				} catch (XMPPException e) {
					Log.e("MsgError", "删除已获取的离线信息失败");
					e.printStackTrace();
				}
				Presence presences = new Presence(Presence.Type.available);
				myContext.getConn().sendPacket(presences);
			}
		}).start();

	}

	public void addGroupChangeListener() {
	}

	/**
	 * 创建chat监听器
	 */
	private void addChatListener() {
		myContext.getChatManager().addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {

				Out.println("main thread " + chat.getThreadID());
				/**
				 * 判断Chat是否由用户主动创建
				 */
				if (!createdLocally) {
					myContext.getChatList().add(chat);
					chat.addMessageListener(new MessageListener() {
						@Override
						public void processMessage(final Chat chat,
								final Message message) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ChatMsgEntity recMsg = new ChatMsgEntity(
											Date.getDate(), true, false,
											ChatUtil.getPartnerName(chat),
											message.getBody(), null);
									chatDao.add(recMsg);
									session = new Session(ChatUtil
											.getPartnerName(chat), message
											.getBody(), Date.getDate());
									/*
									 * 更新会话
									 */
									for (Session s : sessionList) {
										if (s.getName().equals(
												ChatUtil.getPartnerName(chat))) {
											sessionList.remove(s);
											break;
										}
									}
									sessionList.add(session);
									sessionsAdapter.notifyDataSetChanged();
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
			for (RosterEntry entry : myContext.getRoster().getEntries()) {
				if (entry.getName().equals(sessionList.get(position).getName())) {
					tEntry = entry;
					break;
				}
			}
			Entry entry = new Entry(tEntry.getName(), getPresence(tEntry),
					tEntry.getUser());
			bundle.putSerializable("entry", entry);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private void init() {
		instance = this;
		myContext = (MyContext) getApplication();
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

	// 点击了添加好友按钮
	public void addFriend(View v) {
		Intent intent = new Intent(MainChat.this, SearchFriend.class);
		startActivity(intent);
	}

	// 设置标题栏右侧按钮的作用
	public void btnmainright(View v) {
		Intent intent = new Intent(MainChat.this, MainTopRightDialog.class);
		startActivity(intent);
	}

	public void startchat(View v) { // 小黑 对话界面
		Intent intent = new Intent(MainChat.this, ChatActivity.class);
		startActivity(intent);
	}

	public void btn_shake(View v) { // 手机摇一摇
		Intent intent = new Intent(MainChat.this, ShakeActivity.class);
		startActivity(intent);
	}

	// 退出 伪“对话框”，其实是一个activity
	public void exit_settings(View v) {
		Intent intent = new Intent(MainChat.this, ExitFromSettings.class);
		startActivity(intent);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ChatMsgEntity msg = (ChatMsgEntity) intent.getExtras()
					.getSerializable("msg");
			if (msg != null) {
				String name = (msg.getPartner() != null ? msg.getPartner()
						: msg.getGroupName());
				for (Session s : sessionList) {
					if (s.getName().equals(
							msg.getPartner() != null ? msg.getPartner() : msg
									.getGroupName())) {
						sessionList.remove(s);
						break;
					}
				}
				session = new Session(name, msg.getText(), msg.getDate());
				sessionList.add(session);
				handler.sendMessage(new android.os.Message());
			}
		}
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
					getPresence(rosterEntry), rosterEntry.getUser());
			bundle.putSerializable("entry", entry);
			intent.putExtras(bundle);
			startActivity(intent);
			childData.clear();
			return false;
		}
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

	public class MyOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final int groupPosition = (Integer) view.getTag(R.id.exit_layout);
			final int childPosition = (Integer) view.getTag(R.id.exit_layout2);
			if (childPosition != -1) {
				Builder dialog = new AlertDialog.Builder(MainChat.this);
				dialog.setTitle("好友操作");
				dialog.setItems(new String[] { "查看资料", "删除好友", "移至分组", },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								RosterEntry entry = null;
								switch (which) {
								case 0:
									// 一个case执行后dialog自动消失
									childData.addAll(groupData.get(
											groupPosition).getEntries());
									entry = childData.get(childPosition);
									Intent intent = new Intent();
									intent.putExtra("user", entry.getUser());
									intent.setClass(MainChat.this,
											FriendInfo.class);
									startActivity(intent);
									break;
								case 1:
									childData.addAll(groupData.get(
											groupPosition).getEntries());
									entry = childData.get(childPosition);
									try {
										myContext.getRoster()
												.removeEntry(entry);
									} catch (XMPPException e) {
										Log.e("error", "删除好友失败");
										e.printStackTrace();
									} finally {
										childData.clear();
									}
									break;
								case 2:
									// 改变分组暂没实现
									// childData.addAll(groupData.get(
									// groupPosition).getEntries());
									// entry = childData.get(childPosition);
									// Intent intent=new Intent();
									// intent.putExtra("group", groupPosition);
									// startActivityForResult(intent,
									// groupChanged);
									break;
								default:
								}
							}
						})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}

								}).create().show();
			} else {
				new Builder(MainChat.this)
						.setPositiveButton("分组管理", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(MainChat.this,
										GroupManage.class);
								startActivityForResult(intent, 1);
								dialog.cancel();
							}
						}).create().show();
			}
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 只要 是从分组管理返回的，就更新数据
		if (requestCode == 1) {
			boolean flag = data.getExtras().getBoolean("groupChanged");
			if (flag) {
				expandListDataChanged();
			}
		}
	}

	public String getPresence(RosterEntry re) {
		boolean presence = myContext.getRoster().getPresence(re.getUser())
				.isAvailable();
		if (presence) {
			return myContext.getRoster().getPresence(re.getUser()).getStatus();
		} else {
			return new String("离线");
		}
	}

	@Override
	public void finish() {
		super.finish();
		unregisterReceiver(myReceiver);
	}
}
