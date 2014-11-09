package com.ichat.mode;

import java.io.Serializable;

import org.jivesoftware.smack.packet.Packet;

public class Session implements Serializable{
	private String name;
	private String last_content;
	private String date;
	private Packet packet=null;
	public Session() {}
	
	public Session(String name, String last_content, String date) {
		super();
		this.name = name;
		this.last_content = last_content;
		this.date = date;
	}
	public Session(String name, String last_content, String date,Packet packet) {
		super();
		this.name = name;
		this.last_content = last_content;
		this.date = date;
		this.packet=packet;
	}
	

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLast_content() {
		return last_content;
	}
	public void setLast_content(String last_content) {
		this.last_content = last_content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
