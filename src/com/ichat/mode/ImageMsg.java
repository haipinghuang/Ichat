package com.ichat.mode;

import java.io.Serializable;

public class ImageMsg implements Serializable{
	private Type type;
	private String imagePath;
	public Type getType() {
		return type;
	}
	public ImageMsg(Type type, String imagePath) {
		super();
		this.type = type;
		this.imagePath = imagePath;
	}

	public ImageMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public enum Type{
		init,
		receive_finish
	}
}
