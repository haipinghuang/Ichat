package com.ichat.mode;

import java.io.Serializable;

public class Session implements Serializable{
	private String name;
	private String last_content;
	private String date;
	public Session() {}
	
	public Session(String name, String last_content, String date) {
		super();
		this.name = name;
		this.last_content = last_content;
		this.date = date;
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
