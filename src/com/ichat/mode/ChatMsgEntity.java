package com.ichat.mode;

import java.io.Serializable;

public class ChatMsgEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String date;
	private String text;
	private boolean isComMeg = true;
	private boolean isAcked = true;
	private String partner; //群组名 或 联系人名
	private String groupName;
	public String getDate() {
		return date;
	}
	public String getPartner() {
		return partner;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}


	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public boolean isAcked() {
		return isAcked;
	}

	public void setAcked(boolean isAcked) {
		this.isAcked = isAcked;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public ChatMsgEntity() {
	}
	public ChatMsgEntity(String date, boolean isComMsg,
			boolean isAcked, String partner, String text,String groupName) {
		super();
		this.date = date;
		this.text = text;
		this.isComMeg = isComMsg;
		this.isAcked = isAcked;
		this.partner = partner;
		this.groupName = partner;
	}
	public ChatMsgEntity(String date, String text, boolean isComMsg) {
		super();
		this.date = date;
		this.text = text;
		this.isComMeg = isComMsg;
	}

}
