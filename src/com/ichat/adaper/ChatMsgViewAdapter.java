package com.ichat.adaper;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ichat.activity.R;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.util.SmileUtils;
public class ChatMsgViewAdapter extends BaseAdapter {
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

	private List<ChatMsgEntity> coll;
	private Context context;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
		this.coll = coll;
		this.context=context;
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		ChatMsgEntity msg = coll.get(position);

		if (msg.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	}
	
	public int getViewTypeCount() {
		return 2;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsgEntity msg = coll.get(position);
		boolean isComMsg = msg.getMsgType();
		ViewHolder viewHolder=null;
		if (convertView == null) {
//			Out.println("convertView == null");
			if (isComMsg) {
				convertView = View.inflate(context,R.layout.chatting_msg_coming,
						null);
			} else {
				convertView = View.inflate(context,R.layout.chatting_msg_sending,
						null);
			}
			viewHolder = new ViewHolder();
			viewHolder.layout=(LinearLayout) convertView.findViewById(R.id.layout);
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
//			viewHolder.isComMsg = isComMsg;
			viewHolder.imageView=(ImageView) convertView.findViewById(R.id.imageView);
//			if(ChatMsgEntity.Type.image.equals(msg.getType())){
//				viewHolder.imageView=(ImageView) convertView.findViewById(R.id.imageView);
//			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvSendTime.setText(msg.getDate());
		if(ChatMsgEntity.Type.image.equals(msg.getType())){
			viewHolder.tvContent.setVisibility(View.GONE);
			viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.imageView.setImageBitmap(convertToBitmap(msg.getText()));
		}else{
			Spannable span=new SpannableString(msg.getText());
			SmileUtils.addSmiles(context, span);
			viewHolder.tvContent.setVisibility(View.VISIBLE);
			viewHolder.imageView.setVisibility(View.GONE);
			viewHolder.tvContent.setText(span);
			if (isComMsg) {
				viewHolder.layout.setBackgroundResource(R.drawable.chatfrom_bg);
			} else {
				viewHolder.layout.setBackgroundResource(R.drawable.chatto_bg);
			}
		}
		return convertView;
	}
//	private void setImage(ChatMsgEntity msg,ImageView imageView) {
//		ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(context).build();
//		ImageLoader.getInstance().init(config);
//		Out.println("imag path:"+msg.getText());
//		ImageLoader.getInstance().displayImage(msg.getText(), imageView);
//		ImageLoader.getInstance().destroy();
//	}
	private Bitmap convertToBitmap(String pathName){
		Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(pathName, options);
		int height=options.outHeight;
		int width=options.outWidth;
//		DisplayMetrics dm=new DisplayMetrics();
//		dm=context.getResources().getDisplayMetrics();
//		context.getSystemService(android.)
		WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int windowWidth=wm.getDefaultDisplay().getWidth();
		int windowHeight=wm.getDefaultDisplay().getHeight();
		int scale=1;
		int scalex=width/(windowWidth/4);
		int scaley=height/(windowHeight/4);
		if(scalex>scaley & scaley>=1){
			scale=scalex;
		}
		if(scaley>scalex & scalex>=1){
			scale=scaley;
		}
		options.inJustDecodeBounds=false;
		options.inSampleSize=scale;
		return BitmapFactory.decodeFile(pathName,options);
	}
	static class ViewHolder {
		public LinearLayout layout;
		public TextView tvSendTime;
		public TextView tvContent;
		public boolean isComMsg = true;
		public ImageView imageView;
	}

}
