package com.musicweb.domain;

import java.util.Date;

public class Album {
	private int id;
	private String name;
	private String intro;
	private int artistId;
	private String artistName;
	private int playCount;
	private int region;
	private int style;
	private String image;
	private Date releaseDate;

	public Album(String name,Date realeaseDate,int region,int style,String intro,String image,int playCount,int artistId) {
		this.name=name;
		this.releaseDate=realeaseDate;
		this.region=region;
		this.style=style;
		this.intro=intro;
		this.image=image;
		this.playCount=playCount;
		this.artistId=artistId;
	}
	
	public Album(int id,String name,Date realeaseDate,int region,int style,String intro,String image,int playCount,int artistId) {
		this.id=id;;
		this.name=name;
		this.releaseDate=realeaseDate;
		this.region=region;
		this.style=style;
		this.intro=intro;
		this.image=image;
		this.playCount=playCount;
		this.artistId=artistId;
		
	}
	
	public Album() {
		
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

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

}
