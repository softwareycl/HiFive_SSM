package com.musicweb.view;

import java.util.List;

public class PlaylistView {
	private int id;
	private String name;
	private String intro;
	private String image;
	
	private List<SimpleSongView> songList;

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

	public List<SimpleSongView> getSongList() {
		return songList;
	}

	public void setSongList(List<SimpleSongView> songList) {
		this.songList = songList;
	}
}
