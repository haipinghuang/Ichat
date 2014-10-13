package com.ichat.util;

import org.jivesoftware.smack.Chat;

public class ChatUtil {
	/**
	 * 获取对方name
	 * @param chat
	 * @return 对方name
	 */
	public static String getPartnerName(Chat chat){
		int end=chat.getParticipant().lastIndexOf('@');
		return chat.getParticipant().substring(0, end);
	}
}
