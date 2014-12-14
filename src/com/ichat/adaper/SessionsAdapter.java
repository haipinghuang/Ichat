package com.ichat.adaper;
import java.util.List;

import com.ichat.activity.R;
import com.ichat.adaper.EntriesAdapter.ViewHolder;
import com.ichat.mode.Session;
import com.ichat.util.SmileUtils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SessionsAdapter extends BaseAdapter{
	private List<Session> sessionList;
	private Context context;
	public SessionsAdapter(Context context,List<Session> list){
		this.sessionList=list;
		this.context=context;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sessionList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sessionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView=View.inflate(context,R.layout.session, null);
			viewHolder=new ViewHolder();
			viewHolder.name=(TextView) convertView.findViewById(R.id.name);
			viewHolder.last_content=(TextView) convertView.findViewById(R.id.last_content);
			viewHolder.last_time=(TextView) convertView.findViewById(R.id.last_time);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(sessionList.get(position).getName());
		Spannable span=new SpannableString(sessionList.get(position).getLast_content());
		SmileUtils.addSmiles(context, span);
		viewHolder.last_content.setText(span);
		viewHolder.last_time.setText(sessionList.get(position).getDate());
		return convertView;
	}
	static class ViewHolder{
		private TextView name;
		private TextView last_content;
		private TextView last_time;
	}

}
