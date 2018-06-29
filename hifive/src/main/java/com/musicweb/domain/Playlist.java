package com.musicweb.domain;

public class Playlist {
	private int id;
	private String name;
	private String intro;
	private String image;
	private String userId;

	public Playlist(int id, String name, String intro, String image) {
		super();
		this.id = id;
		this.name = name;
		this.intro = intro;
		this.image = image;
	}

	public Playlist() {
		super();
	}

	public Playlist(String name, String intro, String image) {
		super();
		this.name = name;
		this.intro = intro;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
