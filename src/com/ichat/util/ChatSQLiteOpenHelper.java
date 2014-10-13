package com.ichat.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatSQLiteOpenHelper extends SQLiteOpenHelper{



	public ChatSQLiteOpenHelper(Context context) {
		super(context, "chat.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table chat(id integer primary key autoincrement,msg_time char(16)" +
				" not null,msg_dir smallint not null,is_acked smallint not null,partner varchar(20),msg_body text not null" +
				",group_name varchar(20))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}


}
