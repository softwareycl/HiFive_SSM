package com.musicweb.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.UserDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.UserService;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.EmailSenderUtil;
import com.musicweb.util.FileUtil;
import com.musicweb.util.MD5Util;
import com.musicweb.util.RedisUtil;
import com.musicweb.service.CacheService;

/**
 * UserServiceImpl
 * @author likexin
 * @Date 2018.6.23
 * UserServiceImpl完成有关用户模块的业务逻辑实现
 * 接受UserController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给UserController
 * Service层针对业务数据增加各类缓存操作
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Resource
	private UserDao userDao;
	@Resource
	private CacheService cacheService;
	@Resource
	private RedisUtil redisUtil;


	private static final String USER = "user";
	private static final String USER_SONGS = "user_songs";
	private static final String USER_ALBUMS = "user_albums";
	private static final String USER_PLAYLISTS = "user_playlists";
	
	/**
	 * 用户注册
	 * @param user 用户注册信息
	 * @return 注册操作状态
	 */
	@Override
	public boolean register(User user) {
		if(checkUserExisted(user.getId().trim())) return false;
		// 生成用户code
        String code = UUID.randomUUID().toString().replace("-", "");
        user.setActivationCode(code);
		user.setPwd(MD5Util.getMD5(user.getPwd()));
		user.setSecurityAnswer(MD5Util.getMD5(user.getSecurityAnswer()));
		user.setType(-1);
		if(userDao.insert(user)>0) {
			redisUtil.hset(USER, user.getId().trim(), user);
			// 向用户发送激活邮件
            EmailSenderUtil emailSender = new EmailSenderUtil();
        	 try {
 				emailSender.sendMail(user.getId(), code);
 			} catch (RuntimeException | IOException | MessagingException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 				
 			}
			return true;
		}
		return false;
	}

	@Override
	public boolean active(String code) {
		int i = userDao.updateActivation(code);
		return i>0;
	}
	
	/**
	 * 用户登录
	 * @param user 用户信息
	 * @return 登录状态
	 */
	@Override
	public int login(User user) {
		String id = user.getId();
		User userDB = cacheService.getAndCacheUserByUserID(id);
		// 用户不存在
		if(!checkUserExisted(id)) return 2;
		//账号密码匹配错误
		if(!MD5Util.getMD5(user.getPwd()).equals(userDB.getPwd())) return 3;
		if(userDB.getType() == 0)
			//管理员
			return 0;
		else if(userDB.getType() == 1)
			//普通用户
			return 1;
		else if(userDB.getType() == -1){
			//未激活
			return 4;
		}
		return -1;
	}

	/**
	 * 查看用户是否存在
	 * @param id 用户ID
	 * @return 
	 */
	@Override
	public boolean checkUserExisted(String id) {
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user != null;
	}

	/**
	 * 获取用户的密保问题
	 * @param id 用户ID
	 * @return 用户的密保问题
	 */
	@Override
	public int getSecurityQuestion(String id) {
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user.getSecurityQuestion();
	}

	/**
	 * 检验密保问题答案是否正确
	 * @param id 用户id
	 * @param answer 用户输入的密保问题答案
	 * @return 匹配状态
	 */
	@Override
	public boolean checkSecurityAnswer(String id, String answer) {
		answer.trim();
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		return user.getSecurityAnswer().equals(MD5Util.getMD5(answer));
	}

	/**
	 * 重置密码
	 * @param id 用户id
	 * @param pwd 新设密码
	 * @return 操作状态
	 */
	@Override
	public boolean resetPassword(String id, String pwd) {
		pwd.trim();
		id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		String pwdEncoded = MD5Util.getMD5(pwd);
		user.setPwd(pwdEncoded);
		userDao.updatePassword(id, pwdEncoded);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 获取用户信息
	 * @param id 用户id
	 * @return 用户
	 */
	@Override
	public User getInfo(String id) {
		User user = cacheService.getAndCacheUserByUserID(id);
		return user;
	}

	/**
	 * 设置用户头像
	 * @param id 用户id
	 * @param image 图片路径
	 * @return 操作状态
	 */
	@Override
	public boolean setImage(String id, String image) {
		User user = cacheService.getAndCacheUserByUserID(id);
		String imageOld = user.getImage();
		user.setImage(image);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		userDao.updateImage(id, image);
		// 如果上传的图片名与原图片名不同，则删除原图片
		if(!image.equals(imageOld)) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			String userImageFilePath = WebInfoPath + imageOld;
			FileUtil.deleteFile(new File(userImageFilePath));
		}
		return true;
	}

	/**
	 * 用户修改个人信息
	 * @param user
	 * @return 操作状态
	 */
	@Override
	public boolean modifyInfo(User user) {
		User oldUser = cacheService.getAndCacheUserByUserID(user.getId());
		oldUser.setName(user.getName().trim());
		oldUser.setGender(user.getGender());
		userDao.update(oldUser);
		redisUtil.hset(USER, oldUser.getId(), oldUser, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 用户获取我的歌单
	 * @param id 用户id
	 * @return 用户的歌单列表
	 */
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

	/**
	 * 用户获取收藏的音乐
	 * @param userId 用户id
	 * @return 用户喜欢的音乐列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Song> getLikedSongs(String userId) {
		userId.trim();
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		if(object == null) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			songList = userDao.selectLikeSongs(userId);
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
			if(songList != null)
				redisUtil.hset(USER_SONGS, userId, songList, TimeConstant.A_DAY);
		}
		else songList = (List<Song>)object;
		return songList;
	}

	/**
	 * 用户获取收藏的专辑
	 * @param userId 用户ID
	 * @return 用户收藏的专辑列表
	 */
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

	/**
	 * 添加喜欢的专辑
	 * @param userId 用户id
	 * @param albumId 专辑id
	 * @return 操作状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addLikeAlbum(String userId, int albumId) {
		userId.trim();
		List<Album> oldAlbumList = getLikeAlbums(userId);
		for(Album al: oldAlbumList) {
			if(al.getId() == albumId)
				return true;
		}
		
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

	/**
	 * 添加喜欢的歌曲
	 * @param userId 用户id
	 * @param songId 歌曲id
	 * @return 操作状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addLikeSong(String userId, int songId) {
		userId.trim();
		
		List<Song> oldSongList = getLikedSongs(userId);
		for(Song s: oldSongList) {
			if(s.getId() == songId)
				return true;
		}
		
		userDao.insertLikeSong(userId, songId);
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		// 无缓存 从数据库中取出所有歌曲
		if(object == null) {
			songList = userDao.selectLikeSongs(userId);
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//有缓存，取出一首歌
		else {
			songList = (List<Song>)object;
			Song song = cacheService.getAndCacheSongBySongID(songId);
			if(song.getImage()==null) 
				song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
			//设置歌曲时长
			String musicFilePath = WebInfoPath + song.getFilePath();
			song.setDuration(DurationUtil.computeDuration(musicFilePath));
			songList.add(song);
		}
		redisUtil.hset(USER_SONGS, userId, songList, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 移除喜欢的专辑
	 * @param userId 用户id
	 * @param albumId 专辑id
	 * @return
	 */
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
		//找到缓存中的目标专辑并删除
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

	/**
	 * 删除喜欢的歌曲
	 * @param userId 用户id
	 * @param songId 歌曲id
	 * @return 操作状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeLikeSong(String userId, int songId) {
		userId.trim();
		userDao.deleteLikeSong(userId, songId);
		List<Song> songList;
		Object object = redisUtil.hget(USER_SONGS, userId);
		//缓存为空，放入缓存
		if(object == null) {
			songList = userDao.selectLikeSongs(userId);
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			for(Song song : songList) {
				if(song.getImage()==null) 
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//存在缓存， 更新缓存
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

	/**
	 * 修改密码
	 * @param id 用户id
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @return 操作状态
	 */
	@Override
	public boolean modifyPassword(String id, String oldPwd, String newPwd) {
		oldPwd = oldPwd.trim();
		newPwd = newPwd.trim();
		id = id.trim();
		User user = cacheService.getAndCacheUserByUserID(id);
		System.out.println(user.getPwd());
		if(!MD5Util.getMD5(oldPwd).equals(user.getPwd())) return false;
		String pwd = MD5Util.getMD5(newPwd);
		user.setPwd(pwd);
		userDao.updatePassword(id, pwd);
		redisUtil.hset(USER, id, user, TimeConstant.A_DAY);
		return true;
	}

}
