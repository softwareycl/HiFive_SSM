package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.UserDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.UserService;
import com.musicweb.util.MD5Util;
import com.musicweb.util.RedisUtil;
import com.musicweb.service.CacheService;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Resource
	private UserDao userDao;
	@Resource
	private CacheService cacheService;
	@Resource
	private RedisUtil redisUtil;

	@Override
	public boolean register(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	private static final String USER = "user";
	private static final String USER_SONGS = "user_songs";
	private static final String USER_ALBUMS = "user_albums";
	
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Song> getLikedSongs(String userId) {
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		if(object == null) {
			songList = userDao.selectLikeSongs(userId);
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
			}
			if(songList != null)
				redisUtil.hset(USER_SONGS, userId, songList, TimeConstant.A_DAY);
		}
		else songList = (List<Song>)object;
		return songList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Album> getLikeAlbums(String userId) {
		List<Album> albumList;
		Object object = redisUtil.hget(USER_ALBUMS, userId);
		if(object == null) {
			albumList = userDao.selectLikeAlbums(userId);
			if(albumList != null)
				redisUtil.hset(USER_ALBUMS, userId, albumList, TimeConstant.A_DAY);
		}
		else albumList = (List<Album>)object;
		return albumList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addLikeAlbum(String userId, int albumId) {
		userDao.insertLikeAlbum(userId, albumId);
		List<Album> albumList;
		Object object = redisUtil.hget(USER_ALBUMS, userId);
		if(object == null) {
			albumList = userDao.selectLikeAlbums(userId);
		}
		else {
			albumList = (List<Album>)object;
			Album album = cacheService.getAndCacheAlbumByAlbumID(albumId);
			albumList.add(album);
		}
		redisUtil.hset(USER_ALBUMS, userId, albumList, TimeConstant.A_DAY);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addLikeSong(String userId, int songId) {
		userDao.insertLikeSong(userId, songId);
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		if(object == null) {
			songList = userDao.selectLikeSongs(userId);
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
			}
		}
		else {
			songList = (List<Song>)object;
			Song song = cacheService.getAndCacheSongBySongID(songId);
			songList.add(song);
		}
		redisUtil.hset(USER_SONGS, userId, songList, TimeConstant.A_DAY);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeLikeAlbum(String userId, int albumId) {
		userDao.deleteLikeAlbum(userId, albumId);
		List<Album> albumList;
		Object object = redisUtil.hget(USER_ALBUMS, userId);
		if(object == null) {
			albumList = userDao.selectLikeAlbums(userId);
		}
		else {
			albumList = (List<Album>)object;
			for(Album album: albumList) {
				if(albumId == album.getId()) {
					albumList.remove(album);
					break;
				}	
			}
		}
		redisUtil.hset(USER_ALBUMS, userId, albumList, TimeConstant.A_DAY);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeLikeSong(String userId, int songId) {
		userDao.deleteLikeSong(userId, songId);
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		if(object == null) {
			songList = userDao.selectLikeSongs(userId);
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
			}
		}
		else {
			songList = (List<Song>)object;
			for(Song song: songList) {
				if(songId == song.getId()) {
					songList.remove(song);
					break;
				}	
			}
		}
		redisUtil.hset(USER_SONGS, userId, songList, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean modifyPassword(String id, String oldPwd, String newPwd) {
		User user = cacheService.getAndCacheUserByUserID(id);
		if(!MD5Util.getMD5(oldPwd).equals(user.getPwd())) return false;
		String pwd = MD5Util.getMD5(newPwd);
		user.setPwd(pwd);
		userDao.updatePassword(id, pwd);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		return true;
	}
	
}
