package com.ichat.adaper;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import com.ichat.activity.MainChat;
import com.ichat.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandListViewAdapter extends BaseExpandableListAdapter{
	List<RosterEntry> childData=new ArrayList<RosterEntry>();
	private List<RosterGroup> groupData;
	private LayoutInflater inflater;
	public ExpandListViewAdapter(Context context,List<RosterGroup> groupData){
		this.groupData=groupData;
		this.inflater=LayoutInflater.from(context);
	}
	@Override
	public int getGroupCount() {
		return groupData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupData.get(groupPosition).getEntries().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupData.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<RosterEntry> childData=new ArrayList<RosterEntry>();
		childData.addAll(groupData.get(groupPosition).getEntries());
		return childData.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 34);
		if(convertView==null){
			convertView=inflater.inflate(R.layout.group, null);
			convertView.setLayoutParams(lp);
			viewHolder = new ViewHolder();
			viewHolder.groupName=(TextView) convertView.findViewById(R.id.groupName);
			viewHolder.count=(TextView) convertView.findViewById(R.id.count);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		convertView.setTag(R.id.exit_layout, groupPosition);//group
		convertView.setTag(R.id.exit_layout2, -1);//child
		viewHolder.count.setText(groupData.get(groupPosition).getEntries().size()+"");
		viewHolder.groupName.setText(groupData.get(groupPosition).getName());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView=inflater.inflate(R.layout.entry, null);
			viewHolder.username=(TextView) convertView.findViewById(R.id.name);
			viewHolder.status=(TextView) convertView.findViewById(R.id.status);
			viewHolder.introduction=(TextView) convertView.findViewById(R.id.introduction);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		convertView.setTag(R.id.exit_layout, groupPosition);//group
		convertView.setTag(R.id.exit_layout2, childPosition);//child

		childData.addAll(groupData.get(groupPosition).getEntries());
		viewHolder.username.setText(childData.get(childPosition).getName());
		viewHolder.status.setText(MainChat.instance.getPresence(childData.get(childPosition)));
		childData.clear();
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	static class ViewHolder{
		public TextView groupName;
		public TextView count;
		
		public TextView username;
		public TextView status;
		public TextView introduction;
	}
}
