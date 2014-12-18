package com.ichat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ichat.context.MyContext;

public class SearchFriend extends Activity {
	private EditText friendName_et;
	private ListView friendsView_lv;
	private BaseAdapter myadapter;
	private List<String> nameList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_friend);
		friendName_et = (EditText) findViewById(R.id.friendName);
		friendsView_lv = (ListView) findViewById(R.id.lv_friends);
		myadapter = getAdapter();
		friendsView_lv.setAdapter(myadapter);
	}

	public void searchFriend(View v) {
		nameList.clear();
		String friend = friendName_et.getText().toString().trim();
		if (!TextUtils.isEmpty(friend)) {
			nameList.add(friend);
			friendsViewRefresh();
		}else{
			Toast.makeText(this, "请输入要查找的用户", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	public void add(View v) {
		Toast.makeText(this, "124234", Toast.LENGTH_SHORT).show();

	}

	private void friendsViewRefresh() {
		myadapter.notifyDataSetChanged();
	}

	public void returnBack(View v) {
		this.finish();
	}

	/**
	 * 次查找功能暂为实现 目前处理方法是直接显示一个条目.
	 * @param v
	 */
	private BaseAdapter getAdapter() {
		myadapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.friend_in_searchfriend, null);
					viewHolder = new ViewHolder();
					viewHolder.name = (TextView) convertView
							.findViewById(R.id.name);
					viewHolder.button = (Button) convertView
							.findViewById(R.id.add);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.name.setText(nameList.get(position));
				viewHolder.button.setOnClickListener(new MyOnClickListener(
						position));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return nameList.get(position);
			}

			@Override
			public int getCount() {
				return nameList.size();
			}

			class ViewHolder {
				public TextView name;
				public Button button;
			}
		};
		return myadapter;
	}

	class MyOnClickListener implements OnClickListener {
		int index;

		@Override
		public void onClick(View v) {
			String friend = nameList.get(index);
			Intent intent = new Intent(SearchFriend.this, FriendAdd.class);
			intent.putExtra("friend", friend);
			startActivity(intent);
		}

		public MyOnClickListener(int index) {
			this.index = index;
		}

	}

}
