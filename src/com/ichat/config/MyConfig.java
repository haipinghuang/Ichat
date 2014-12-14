package com.ichat.config;

public class MyConfig{
	public static final String host="10.0.2.2";
	public static final int port=5222;
	public static final String SYNC_MSG_ACTION="com.ichat.myReceiver.syncMsg";
	public static final String RECEIVE_IMG_ACTION="com.ichat.myReceiver.receiveImg";
	//根据服务器分组聊天服务名称配置
	public static final String conferenceService="@conference.wechat.com";
	public static int isMediaOn=1;
	public static int isNotificationOn=1;
}
