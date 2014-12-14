package com.ichat.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ichat.mode.ChatMsgEntity;
import com.ichat.util.BooleanUtil;
import com.ichat.util.ChatSQLiteOpenHelper;

public class ChatDao {
	ChatSQLiteOpenHelper helper=null;
	SQLiteDatabase db=null;
	public ChatDao(Context context){
		helper=new ChatSQLiteOpenHelper(context,1);
	}
	public void add(ChatMsgEntity msg){
		db= helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("msg_time", msg.getDate());
		values.put("is_acked", BooleanUtil.parseBoolean(msg.isAcked()));
		values.put("partner", msg.getPartner());
		values.put("group_name", msg.getGroupName());
		values.put("msg_body", msg.getText());
		values.put("msg_dir", BooleanUtil.parseBoolean(msg.isComMeg()));
		values.put("msg_type", msg.getType().toString());
		db.insert("chat", null, values);
		db.close();
	}
	/**
	 * 查询所有的聊天记录
	 * @param partner
	 */
	public List<ChatMsgEntity> find(String partner){
		List<ChatMsgEntity> msgList=new ArrayList<ChatMsgEntity>();
		db= helper.getReadableDatabase();
		String sql="select id,msg_time,msg_dir,msg_body,msg_type from chat where partner=? order by id";
		Cursor cursor=db.rawQuery(sql, new String[]{partner});
		while(cursor.moveToNext()){
			String msgtime=cursor.getString(cursor.getColumnIndex("msg_time"));
			int msg_dir=cursor.getInt(cursor.getColumnIndex("msg_dir"));
			String msg_body=cursor.getString(cursor.getColumnIndex("msg_body"));
			String temp=cursor.getString(cursor.getColumnIndex("msg_type"));
			ChatMsgEntity.Type type=null;
			if("text".equals(temp)){
				type=ChatMsgEntity.Type.text;
			}else if("image".equals(temp)){
				type=ChatMsgEntity.Type.image;
			}
			ChatMsgEntity msg=new ChatMsgEntity(msgtime, msg_body, BooleanUtil.parseInt(msg_dir),type);
			msgList.add(msg);
		}
		db.close();
		return msgList;
	}
	
}
