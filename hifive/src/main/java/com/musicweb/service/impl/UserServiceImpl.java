package com.musicweb.service.impl;

import java.io.File;
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
import com.musicweb.util.FileUtil;
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
	private static final String USER_PLAYLISTS = "user_playlists";
	
	@Override
	public int login(User user) {
		String id = user.getId();
		User userDB = cacheService.getAndCacheUserByUserID(id);
		if(!checkUserExisted(id)) return -1;
		if(!MD5Util.getMD5(user.getPwd()).equals(userDB.getPwd())) return -1;
		return 0;
	}

	@Override
	public boolean checkUserExisted(String id) {
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user == null;
	}

	@Override
	public int getSecurityQuestion(String id) {
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user.getSecurityQuestion();
	}

	@Override
	public boolean checkSecurityAnswer(String id, String answer) {
		answer.trim();
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user.getSecurityAnswer().equals(answer);
	}

	@Override
	public boolean resetPassword(String id, String pwd) {
		pwd.trim();
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		String pwdEncoded = MD5Util.getMD5(pwd);
		user.setPwd(pwdEncoded);
		userDao.updatePassword(id, pwd);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public User getInfo(String id) {
		User user = cacheService.getAndCacheUserByUserID(id);
		return user;
	}

	@Override
	public boolean setImage(String id, String image) {
		User user = cacheService.getAndCacheUserByUserID(id);
		String imageOld = user.getImage();
		user.setImage(image);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		userDao.updateImage(id, image);
		if(!image.equals(imageOld)) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf(FileUtil.FILE_SEPARATOR + "classes"));
			String userImageFilePath = WebInfoPath + imageOld;
			FileUtil.deleteFile(new File(userImageFilePath));
		}
		return true;
	}

	@Override
	public boolean modifyInfo(User user) {
		user.setId(user.getId().trim());
		user.setImage(user.getImage().trim());
		user.setName(user.getName().trim());
		user.setPwd(user.getPwd().trim());
		user.setSecurityAnswer(user.getSecurityAnswer().trim());
		userDao.update(user);
		redisUtil.hset(USER, user.getId(), user, TimeConstant.A_DAY);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Playlist> getMyPlaylists(String id) {
		id.trim();
		List<Playlist> playlistList;
		Object object = redisUtil.hget(USER_PLAYLISTS, id);
		if(object == null) {
			playlistList = userDao.selectPlaylists(id);
			if(playlistList != null)
				redisUtil.hset(USER_PLAYLISTS, id, playlistList, TimeConstant.A_DAY);
		}
		else playlistList = (List<Playlist>) object;
		return playlistList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Song> getLikedSongs(String userId) {
		userId.trim();
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
		userId.trim();
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
		userId.trim();
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
		userId.trim();
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
		userId.trim();
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
		userId.trim();
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
		oldPwd.trim();
		newPwd.trim();
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		if(!MD5Util.getMD5(oldPwd).equals(user.getPwd())) return false;
		String pwd = MD5Util.getMD5(newPwd);
		user.setPwd(pwd);
		userDao.updatePassword(id, pwd);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		return true;
	}
	
}
