package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.UserDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Resource
	private UserDao userDao;

	@Override
	public boolean register(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int login(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkUserExisted(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSecurityQuestion(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkSecurityAnswer(String id, String answer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resetPassword(String id, String pwd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User getInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setImage(String id, String image) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyInfo(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Playlist> getMyPlaylists(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> getLikedSongs(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Album> getLikeAlbums(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addLikeAlbum(String userId, int albumId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addLikeSong(String userId, int songId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLikeAlbum(String userId, int albumId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLikeSong(String userId, int songId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyPassword(String id, String oldPwd, String newPwd) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
