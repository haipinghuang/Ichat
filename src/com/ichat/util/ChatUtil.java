package com.ichat.util;

import org.jivesoftware.smack.Chat;

public class ChatUtil {
	/**
	 * ��ȡ�Է�name
	 * @param chat
	 * @return �Է�name
	 */
	public static String getPartnerName(Chat chat){
		int end=chat.getParticipant().lastIndexOf('@');
		return chat.getParticipant().substring(0, end);
	}
}
