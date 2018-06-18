package com.musicweb.view;

import java.util.Date;
import java.util.List;

public class ArtistView{
	private int id;
	private String name;
	private String initial;
	private String image;
	private int region;
	private String country;
	private String intro;
	private int gender;
	private String birthplace;
	private String occupation;
	private Date birthday;
	private String representative;
	
	private List<SimpleSongView> songList;
	private List<SimpleAlbumView> albumList;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public List<SimpleSongView> getSongList() {
		return songList;
	}

	public void setSongList(List<SimpleSongView> songList) {
		this.songList = songList;
	}

	public List<SimpleAlbumView> getAlbumList() {
		return albumList;
	}

	public void setAlbumList(List<SimpleAlbumView> albumList) {
		this.albumList = albumList;
	}
}
