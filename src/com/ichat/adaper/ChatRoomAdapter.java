package com.ichat.adaper;

import java.util.List;

import com.ichat.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatRoomAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> rooms;

	public ChatRoomAdapter(Context context, List<String> rooms) {
		this.inflater = LayoutInflater.from(context);
		this.rooms = rooms;
	}

	@Override
	public int getCount() {
		return rooms.size();
	}

	@Override
	public Object getItem(int position) {
		return rooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.room_item, null);
			TextView roomName = (TextView) convertView
					.findViewById(R.id.roomName);
			roomName.setText(rooms.get(position).toString());
		} else {

		}
		return convertView;
	}

}
