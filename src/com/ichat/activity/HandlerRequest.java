package com.ichat.activity;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.ichat.context.MyContext;
import com.ichat.util.Out;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HandlerRequest extends Activity {
	private TextView title;
	private ListView group_lv;
	private List<RosterGroup> groupList = new ArrayList<RosterGroup>();
	private MyContext myContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle bundle = getIntent().getExtras();
		myContext = (MyContext) getApplication();
		setContentView(R.layout.change_group);
		title = (TextView) findViewById(R.id.title);
		title.setText("��� " + bundle.getString("to") + " ������");
		group_lv = (ListView) findViewById(R.id.group_lv);

		groupList.addAll(myContext.getRoster().getGroups());
		group_lv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;
				if (convertView == null) {
					viewHolder = new ViewHolder();
					convertView = getLayoutInflater().inflate(R.layout.group2,
							null);
					viewHolder.groupName = (TextView) convertView
							.findViewById(R.id.groupName);
					viewHolder.is_checked = (TextView) convertView
							.findViewById(R.id.is_checked);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.groupName.setText(groupList.get(position).getName());
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

			class ViewHolder {
				public TextView groupName;
				public TextView is_checked;
			}
		});
		group_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView is_checked = (TextView) view
						.findViewById(R.id.is_checked);
				is_checked.setText("��");
				String from = bundle.getString("from");
				String packetID = bundle.getString("packetID");
				String name = from.substring(0, from.lastIndexOf('@'));
				Out.println("name " + name);
				String userJID = name + '@'
						+ myContext.getConn().getServiceName();
				String to = bundle.getString("to");
				try {
					Presence presence = new Presence(Presence.Type.subscribed);
					presence.setPacketID(packetID);
					presence.setFrom(to);
					presence.setTo(from);
					myContext.getConn().sendPacket(presence);
					myContext.getRoster().createEntry(userJID, name,
							new String[] { groupList.get(position).getName() });
				} catch (XMPPException e) {
					Log.e("error", "ͬ�������Ӳ���ӶԷ�Ϊ����ʧ��");
					Toast.makeText(HandlerRequest.this,
							"ͬ�������Ӳ���ӶԷ�Ϊ����ʧ��,������...", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
					return;
				}
				Toast.makeText(HandlerRequest.this, "��Ӻ��������ѷ���",
						Toast.LENGTH_SHORT).show();
				HandlerRequest.this.finish();
			}
		});
	}
	public void return_back(View v){
		this.finish();
	}
}
