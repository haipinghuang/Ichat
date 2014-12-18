package com.ichat.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ichat.context.MyContext;

public class ChangeGroup extends Activity {
	private ListView group_lv;
	private List<RosterGroup> groupList = new ArrayList<RosterGroup>();
	private MyContext myContext;
	private int oldGroupNo=-1; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_group);
		myContext = (MyContext) getApplication();
		group_lv = (ListView) findViewById(R.id.group_lv);

		oldGroupNo= (Integer) getIntent().getExtras().get("groupPosition");

		groupList.addAll(myContext.getRoster().getGroups());
		group_lv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder=null;
				if(convertView==null){
					viewHolder=new ViewHolder();
					convertView=getLayoutInflater().inflate(R.layout.group2, null);
					viewHolder.groupName=(TextView) convertView.findViewById(R.id.groupName);
					viewHolder.is_checked=(TextView) convertView.findViewById(R.id.is_checked);
					convertView.setTag(viewHolder);
				}else{
					viewHolder=(ViewHolder) convertView.getTag();
				}
				viewHolder.groupName.setText(groupList.get(position).getName());
				if(oldGroupNo==position){
					viewHolder.is_checked.setText("¡Ì");
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return groupList.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return groupList.size();
			}
			class ViewHolder{
				public TextView groupName;
				public TextView is_checked;
			}
		});
		group_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != oldGroupNo) {
					sendResult(position);
				} else {
					
				}

			}

			
		});
	}
	private void sendResult(int position) {
		Intent intent=getIntent();
		int requestCode=intent.getExtras().getInt("requestCode");
		intent.putExtra("newGroupNo", position);
		setResult(requestCode, intent);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ChangeGroup.this.finish();
	}
	public void return_back(View v){
		sendResult(oldGroupNo);
		this.finish();
		
	}
}
