package com.ichat.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.RosterGroup;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ichat.context.MyContext;

public class GroupManage extends Activity {
	private ListView group_lv;
	private MyContext myContext;
	private MyListViewAdapter myListViewAdapter;
	private LayoutInflater inflator;
	private List<RosterGroup> groupList = new LinkedList<RosterGroup>();
	private boolean groupChanged=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_manage);
		inflator = getLayoutInflater();
		myContext = (MyContext) getApplicationContext();
		groupList.addAll(myContext.getRoster().getGroups());

		group_lv = (ListView) findViewById(R.id.group_lv);
		myListViewAdapter = new MyListViewAdapter();
		group_lv.setAdapter(myListViewAdapter);
		group_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new Builder(GroupManage.this)
						.setTitle("确定")
						.setMessage(
								"确定删除分组:" + groupList.get(position).getName()
										+ " ?")
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Iterator<RosterGroup> it = myContext
										.getRoster().getGroups().iterator();
								while (it.hasNext()) {
									RosterGroup group = it.next();
									if (group == groupList.get(position)) {
//										group.getEntries().clear();
//										break;
									}
								}
								groupList.clear();
								groupList.addAll(myContext.getRoster()
										.getGroups());
								myListViewAdapter.notifyDataSetChanged();
							}
						}).create().show();
			}
		});
	}

	public void addGroup(View v) {
		final EditText groupName = new EditText(this);
		new Builder(GroupManage.this).setTitle("请输入分组名称").setView(groupName)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = groupName.getText().toString().trim();
						if (!TextUtils.isEmpty(name)) {
							myContext.getRoster().createGroup(name);
							groupChanged=true;
							groupList.clear();
							groupList.addAll(myContext.getRoster().getGroups());
							myListViewAdapter.notifyDataSetChanged();
						} else {
						}
						dialog.cancel();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create().show();
	}

	public void managerfinish(View v) {
		this.finish();
	}
	
	@Override
	public void finish() {
		Intent intent=getIntent();
		int requestCode=intent.getExtras().getInt("requestCode");
		intent.putExtra("groupChanged", groupChanged);
		setResult(requestCode, intent);
		super.finish();
	}

	public class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return groupList.size();
		}

		@Override
		public Object getItem(int position) {
			return groupList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflator.inflate(R.layout.group, null);
				viewHolder.groupName = (TextView) convertView
						.findViewById(R.id.groupName);
				viewHolder.action = (TextView) convertView
						.findViewById(R.id.count);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.groupName.setText(groupList.get(position).getName());
			viewHolder.groupName.setTextSize(20);
			viewHolder.action.setText("删除");
			viewHolder.action.setTextSize(20);
			return convertView;
		}

		class ViewHolder {
			public TextView groupName;
			public TextView action;
		}

	}
}
