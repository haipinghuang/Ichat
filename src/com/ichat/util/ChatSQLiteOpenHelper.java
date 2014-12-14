package com.ichat.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatSQLiteOpenHelper extends SQLiteOpenHelper{
	
	public ChatSQLiteOpenHelper(Context context,int version) {
		super(context,"chat.db" , null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table chat(id integer primary key autoincrement,msg_time char(16)" +
				" not null,msg_dir smallint not null,is_acked smallint not null,partner varchar(20),msg_type char(10) not null,msg_body text not null" +
				",group_name varchar(20))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion==2){
//			String sql="alter table chat add column type varchar(10)";
//			db.execSQL(sql);
//			System.out.println("db onUpgrade");
		}
		
	}


}
