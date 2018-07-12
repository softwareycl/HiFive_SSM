package com.musicweb.controller;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.constant.UserConstant;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.PlaylistService;
import com.musicweb.service.UserService;
import com.musicweb.service.AlbumService;
import com.musicweb.view.LoginUserView;
import com.musicweb.view.UserView;
import com.musicweb.view.MyMusicView;
import com.musicweb.view.RegisterUserView;
import com.musicweb.view.SimpleAlbumView;
import com.musicweb.view.SimplePlaylistView;
import com.musicweb.view.SimpleSongView;
import com.musicweb.view.UserAnswerView;

/**
 * UserController
 * UserContoller负责接收前端的有关用户模块的请求，并调用Service层的服务，业务完成后返回结果给前端
 * 用户模块的操作除注册外都是在登录状态下进行，所以该类的方法都带有参数session，用以获得用户id
 * 
 * @author likexin
 * @Date 2018.6.21
 */
@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	private UserService userService;
	@Resource
	private PlaylistService playlistService;
	@Resource
	private AlbumService albumService;
	
	private String testUserId = "public@qq.com";
	private String testAdminId = "public2@qq.com";

	/**
	 * 用户注册
	 * 
	 * @param registerUserView 用户注册视图
	 * @return 操作状态
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Boolean register(@RequestBody RegisterUserView registerUserView) {
		User user = new User();
		BeanUtils.copyProperties(registerUserView, user);
		//注册的类型为用户
		user.setType(1);
		//注册时用户头像为默认头像
		if(user.getGender() == 1)
			user.setImage("/image/user/default1.jpg");
		else
			user.setImage("/image/user/default2.jpg");
		return userService.register(user);
	}
	
	/**
	 * 激活用户
	 * 
	 * @param code 激活码
	 * @return true表示激活成功,false表示激活失败
	 */
	@RequestMapping(value = "/active/{code}", method = RequestMethod.GET)
	@ResponseBody
	public Boolean activeUser(@PathVariable String code) {
		return userService.active(code);
	}

	/**
	 * 用户登录
	 * 
	 * @param loginUserView 登录用户视图
	 * @param session 连接回话
	 * @return 登录状态
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Integer login(@RequestBody LoginUserView loginUserView, HttpSession session) {
		User user = new User();
		BeanUtils.copyProperties(loginUserView, user);
		int status = userService.login(user);
		//用户登录成功则在session中存入用户id
		if(status == 1)
			session.setAttribute(UserConstant.USER_ID, user.getId());
		else if(status == 0)
			session.setAttribute(UserConstant.ADMIN_ID, user.getId());
		return status;
	}
	
	/**
	 * 用户登出
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Boolean logout(HttpSession session) {//get
		session.removeAttribute(UserConstant.USER_ID);
		session.removeAttribute(UserConstant.ADMIN_ID);
		return true;
	}
	
	/**
	 * 显示用户详情
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public UserView showInfo(HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		//id为空说明用户已离线，返回空
		if(id == null) return null;
		UserView userView = new UserView();
		User user = userService.getInfo(id);
		BeanUtils.copyProperties(user, userView);
		return userView;
	}
	
	/**
	 * 用户修改个人信息
	 * 
	 * @param userView 用户视图
	 * @param session 获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyInfo(@RequestBody UserView userView, HttpSession session) {//post
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		//id为空说明用户已离线，返回失败
		if(id == null) return false;
		User user = new User();
		BeanUtils.copyProperties(userView, user);
		return userService.modifyInfo(user);
	}
	
	/**
	 * 用户修改密码
	 * 
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码	
	 * @param session 获取用户id
	 * @return
	 */
	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyPassword(@RequestBody UserAnswerView userAnswer, HttpSession session) {//post
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
	
		if(id == null ) return false;
		
		System.out.println(userAnswer.getOldPwd());
		System.out.println(userAnswer.getNewPwd());
		
		return userService.modifyPassword(id, userAnswer.getOldPwd(), userAnswer.getNewPwd());
	}
	
	/**
	 * 显示我的音乐
	 * 
	 * @param session 获取用户id
	 * @return 我的音乐视图
	 */
	@RequestMapping(value = "/showMyMusic", method = RequestMethod.GET)
	@ResponseBody
	public MyMusicView showMyMusic(HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		if(id == null ) return null;
		MyMusicView myMusicView = new MyMusicView();
		
		//getUserInfo
		BeanUtils.copyProperties(userService.getInfo(id), myMusicView);
		
		//getMyPlaylists
		ArrayList<SimplePlaylistView> simplePlaylistListViewList = new ArrayList<SimplePlaylistView>();
		List<Playlist> playlistList = userService.getMyPlaylists(id);
		for(Playlist playlist: playlistList) {
			SimplePlaylistView view = new SimplePlaylistView();
			BeanUtils.copyProperties(playlist, view);
			view.setCount(playlistService.getSongList(view.getId()).size());
			simplePlaylistListViewList.add(view);
		}
		myMusicView.setPlaylistList(simplePlaylistListViewList);
		myMusicView.setPlaylistCount(simplePlaylistListViewList.size());
		
		//getLikedSongs
		ArrayList<SimpleSongView> simpleSongViewList = new ArrayList<SimpleSongView>();
		List<Song> songList = userService.getLikedSongs(id);
		for(Song song : songList) {
			SimpleSongView view = new SimpleSongView();
			BeanUtils.copyProperties(song, view);
			simpleSongViewList.add(view);
		}
		myMusicView.setLikeSongList(simpleSongViewList);
		myMusicView.setLikeSongCount(simpleSongViewList.size());
		
		//getLikeAlbums
		ArrayList<SimpleAlbumView> simpleAlbumViewList = new ArrayList<SimpleAlbumView>();
		List<Album> albumList = userService.getLikeAlbums(id);
		for(Album album : albumList) {
			SimpleAlbumView view = new SimpleAlbumView();
			BeanUtils.copyProperties(album, view);
			view.setCount(albumService.getSongList(album.getId()).size());
			simpleAlbumViewList.add(view);
		}
		myMusicView.setLikeAlbumList(simpleAlbumViewList);
		myMusicView.setLikeAlbumCount(simpleAlbumViewList.size());
		
		return myMusicView;
	}
	
	/**
	 * 判断用户id是否存在
	 * 
	 * @param id 用户ID
	 * @return 判断结果
	 */
	@RequestMapping(value = "/checkUserExisted", method = RequestMethod.GET)
	@ResponseBody
	public Boolean checkUserExisted(String id) {//get
		return userService.checkUserExisted(id);
	}
	
	/**
	 * 获取用户的密保问题
	 * 
	 * @param id 用户id
	 * @return 密保问题
	 */
	@RequestMapping(value = "/getQuestion", method = RequestMethod.GET)
	@ResponseBody
	public int showSecurityQuestion(String id) {//get
		return userService.getSecurityQuestion(id);
	}
	
	/**
	 * 判断密保问题答案是否正确
	 * 
	 * @param id 用户id
	 * @param securityAnswer 密保问题答案
	 * @param session 用以设置用户id
	 * @return 判断结果
	 */
	@RequestMapping(value = "/checkAnswer", method = RequestMethod.POST)
	@ResponseBody
	public Boolean checkSecurityAnswer(@RequestBody UserAnswerView userAnswer, HttpSession session) {//post
		System.out.println(userAnswer.getId());
		System.out.println(userAnswer.getSecurityAnswer());
		boolean status = userService.checkSecurityAnswer(userAnswer.getId(), userAnswer.getSecurityAnswer());
		//回答正确将用户id存入session
		if(status) 
			session.setAttribute(UserConstant.USER_ID, userAnswer.getId());
		return status;
	}
	
	/**
	 * 重设密码
	 * 
	 * @param newPwd 新密码
	 * @param session 用以获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public Boolean resetPassword(@RequestBody UserAnswerView userAnswer, HttpSession session) {//post
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		System.out.println(userAnswer.getNewPwd());
		
		//test
		//id = testUserId;
		
		if(id == null ) return null;
		return userService.resetPassword(id, userAnswer.getNewPwd());
	}
	
	/**
	 * 收藏歌曲
	 * 
	 * @param songId 歌曲id
	 * @param session 获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/likeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean likeASong(int songId, HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		if(id == null ) return false;
		return userService.addLikeSong(id, songId);
	}
	
	/**
	 * 取消收藏歌曲
	 * 
	 * @param songId 歌曲id
	 * @param session 获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/unlikeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean unlikeASong(int songId, HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		if(id == null ) return false;
		return userService.removeLikeSong(id, songId);
	}
	
	/**
	 * 收藏专辑
	 * 
	 * @param albumId 专辑id
	 * @param session 获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/likeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean likeAnAlbum(int albumId, HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		if(id == null ) return false;
		return userService.addLikeAlbum(id, albumId);
	}
	
	/**
	 * 取消收藏专辑
	 * 
	 * @param albumId 专辑id
	 * @param session 获取用户id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/unlikeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean unlikeAnAlbum(int albumId, HttpSession session) {//get
		String id = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//id = testUserId;
		
		if(id == null ) return false;
		return userService.removeLikeAlbum(id, albumId);
	}
	
}
