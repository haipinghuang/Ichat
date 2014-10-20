package com.ichat.adaper;

import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ichat.activity.MainChat;
import com.ichat.activity.R;
import com.ichat.util.MyContext;

public class EntriesAdapter extends BaseAdapter {
	private List<RosterEntry> entryList;
	private LayoutInflater inflater;
	@Override
	public int getCount() {
		return this.entryList.size();
	}

	public EntriesAdapter(Context context, List<RosterEntry> entryList) {
		this.entryList = entryList;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getItem(int arg0) {
		return this.entryList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.entry, null);
			viewHolder=new ViewHolder();
			viewHolder.name=(TextView) convertView.findViewById(R.id.name);
			viewHolder.status=(TextView) convertView.findViewById(R.id.status);
			viewHolder.introduction=(TextView) convertView.findViewById(R.id.introduction);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(entryList.get(position).getName());
		viewHolder.status.setText(MainChat.instance.getPresence(entryList.get(position)));
		return convertView;
	}

	static class ViewHolder {
		public TextView name;
		public TextView status;
		public TextView introduction;
	}

}
