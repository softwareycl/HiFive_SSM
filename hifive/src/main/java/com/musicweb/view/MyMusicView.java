package com.musicweb.view;

import java.util.List;

public class MyMusicView {
	private String id;
	private String name;
	private String image;
	private String gender;
	private int likeSongCount;
	private int likeAlbumCount;
	private int playlistCount;

	private List<SimpleSongView> likeSongList;
	private List<SimpleAlbumView> likeAlbumList;
	private List<SimplePlaylistView> playlistList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getLikeSongCount() {
		return likeSongCount;
	}

	public void setLikeSongCount(int likeSongCount) {
		this.likeSongCount = likeSongCount;
	}

	public int getLikeAlbumCount() {
		return likeAlbumCount;
	}

	public void setLikeAlbumCount(int likeAlbumCount) {
		this.likeAlbumCount = likeAlbumCount;
	}

	public int getPlaylistCount() {
		return playlistCount;
	}

	public void setPlaylistCount(int playlistCount) {
		this.playlistCount = playlistCount;
	}

	public List<SimpleSongView> getLikeSongList() {
		return likeSongList;
	}

	public void setLikeSongList(List<SimpleSongView> likeSongList) {
		this.likeSongList = likeSongList;
	}

	public List<SimpleAlbumView> getLikeAlbumList() {
		return likeAlbumList;
	}

	public void setLikeAlbumList(List<SimpleAlbumView> likeAlbumList) {
		this.likeAlbumList = likeAlbumList;
	}

	public List<SimplePlaylistView> getPlaylistList() {
		return playlistList;
	}

	public void setPlaylistList(List<SimplePlaylistView> playlistList) {
		this.playlistList = playlistList;
	}

}
