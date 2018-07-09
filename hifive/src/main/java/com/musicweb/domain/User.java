package com.musicweb.domain;

public class User {
	private String id;
	private String name;
	private String pwd;
	private String image;
	private int gender;
	private int securityQuestion;
	private String securityAnswer;
	private String activationCode;
	private int type;

	public User() {}
	
	public User(String id,String name,String pwd,String image,int gender,int securityQuestion,String securityAnswer,int type) {
		this.id=id;
		this.name=name;
		this.pwd=pwd;
		this.image=image;
		this.gender=gender;
		this.securityQuestion=securityQuestion;
		this.securityAnswer=securityAnswer;
		this.type=type;
	}
	
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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(int securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

}
