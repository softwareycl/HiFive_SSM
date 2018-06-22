package com.musicweb.domain;

import java.util.Date;

public class Artist {
	private int id;
	private String name;
	private String initial;
	private String image;
	private int region;
	private String country;
	private String intro;
	private int playCount;
	private int gender;
	private String birthplace;
	private String occupation;
	private Date birthday;
	private String representative;

	public Artist() {
		
	}
	
	public Artist(
			int id,
			String name,
			String initial,
			String image,
			int region,
			String country,
			String intro,
			int playCount,
			int gender,
			String birthplace,
			String occupation,
			Date birthday,
			String representative) {
		this.id=id;
		this.name=name;
		this.initial=initial;
		this.image=image;
		this.region=region;
		this.country=country;
		this.intro=intro;
		this.playCount=playCount;
		this.gender=gender;
		this.birthplace=birthplace;
		this.occupation=occupation;
		this.birthday=birthday;
		this.representative=representative;
	}
	
	public Artist(
			String name,
			String initial,
			String image,
			int region,
			String country,
			String intro,
			int playCount,
			int gender,
			String birthplace,
			String occupation,
			Date birthday,
			String representative) {
		this.name=name;
		this.initial=initial;
		this.image=image;
		this.region=region;
		this.country=country;
		this.occupation=occupation;
		this.intro=intro;
		this.playCount=playCount;
		this.gender=gender;
		this.birthplace=birthplace;
		this.birthday=birthday;
		this.representative=representative;
	}
	
	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
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

}
