package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;

public interface UserService {
	// 注册
	boolean register(User user);

	// 登录
	int login(User user);

	// 找回密码
	boolean checkUserExisted(String id);

	int getSecurityQuestion(String id);

	boolean checkSecurityAnswer(String id, String answer);

	boolean resetPassword(String id, String pwd);

	// 修改个人信息
	User getInfo(String id);

	boolean setImage(String id, String image);

	boolean modifyInfo(User user);

	// 我的音乐界面
	List<Playlist> getMyPlaylists(String id);

	List<Song> getLikedSongs(String userId);

	List<Album> getLikeAlbums(String userId);

	boolean addLikeAlbum(String userId, int albumId);

	boolean addLikeSong(String userId, int songId);

	boolean removeLikeAlbum(String userId, int albumId);

	boolean removeLikeSong(String userId, int songId);
	
	//新增，待确定
	boolean modifyPassword(String id, String oldPwd, String newPwd);
	
}
