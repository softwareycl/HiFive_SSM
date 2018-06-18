package com.musicweb.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.constant.UserConstant;
import com.musicweb.service.PlaylistService;
import com.musicweb.service.UserService;
import com.musicweb.view.LoginUserView;
import com.musicweb.view.UserView;
import com.musicweb.view.MyMusicView;
import com.musicweb.view.RegisterUserView;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	private UserService userService;
	@Resource
	private PlaylistService playlistService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Boolean register(RegisterUserView registerUser) {
		return false;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Integer login(LoginUserView loginUser, HttpSession session) {
		session.setAttribute(UserConstant.USER_ID, "12");
		return 0;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Boolean logout(HttpSession session) {//get
		return false;
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public UserView showInfo(HttpSession session) {//get
		return null;
	}
	
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyInfo(UserView user, HttpSession session) {//post
		return false;
	}
	
	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyPassword(String oldPwd, String newPwd, HttpSession session) {//post
		return false;
	}
	
	@RequestMapping(value = "/showMyMusic", method = RequestMethod.GET)
	@ResponseBody
	public MyMusicView showMyMusic(HttpSession session) {//get
		MyMusicView myMusic = new MyMusicView();
		//getUserInfo
		//getMyPlaylists
		//getLikedSongs
		//getLikeAlbums
		//注入myMusic
		return myMusic;
	}
	
	@RequestMapping(value = "/checkUserExisted", method = RequestMethod.GET)
	@ResponseBody
	public Boolean checkUserExisted(String id) {//get
		return false;
	}
	
	@RequestMapping(value = "/getQuestion", method = RequestMethod.GET)
	@ResponseBody
	public String showSecurityQuestion(String id) {//get
		return null;
	}
	
	@RequestMapping(value = "/checkAnswer", method = RequestMethod.POST)
	@ResponseBody
	public Boolean checkSecurityAnswer(String id, String securityAnswer, HttpSession session) {//post
		return null;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public Boolean resetPassword(String newPwd, HttpSession session) {//post
		return false;
	}
	
	@RequestMapping(value = "/likeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean likeASong(int id, HttpSession session) {//get
		return false;
	}
	
	@RequestMapping(value = "/unlikeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean unlikeASong(int id, HttpSession session) {//get
		return false;
	}
	
	@RequestMapping(value = "/likeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean likeAnAlbum(int id, HttpSession session) {//get
		return false;
	}
	
	@RequestMapping(value = "/unlikeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean unlikeAnAlbum(int id, HttpSession session) {//get
		return false;
	}
	
}
