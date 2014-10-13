package com.ichat.mode;

import java.io.Serializable;

import org.jivesoftware.smack.Chat;

public class Entry implements Serializable{
	private String status;
	private String userJID;
	private String partner;
	public Entry() {
	}
	
	public Entry(String partner,String status,String userJID) {
		this.status=status;
		this.userJID=userJID;
		this.partner=partner;
	}

	
	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getUserJID() {
		return userJID;
	}

	public void setUserJID(String userJID) {
		this.userJID = userJID;
	}


	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
