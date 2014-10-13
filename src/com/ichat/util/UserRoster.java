package com.ichat.util;

import org.jivesoftware.smack.RosterEntry;

import com.ichat.mode.MyContext;

public class UserRoster{
	public static String getPresence(RosterEntry re){
		boolean presence=MyContext.getInstance().getRoster().getPresence(re.getUser()).isAvailable();
		if(presence){
			return MyContext.getInstance().getRoster().getPresence(re.getUser()).getStatus();
		}else{
			return new String("¿Îœﬂ");
		}
	}
}
