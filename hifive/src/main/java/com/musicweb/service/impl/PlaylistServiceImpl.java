package com.musicweb.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.dao.UserDao;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.service.PlaylistService;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.FileUtil;
import com.musicweb.util.RedisUtil;
import com.musicweb.service.CacheService;

/**
 * PlaylistServiceImpl
 * @author likexin
 * @Date 2018.6.21
 * PlaylistServiceImpl完成有关歌单模块的业务逻辑实现
 * 接受PlaylistController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给PlaylistController
 * Service层针对业务数据增加各类缓存操作
 */
@Service("playlistService")
public class PlaylistServiceImpl implements PlaylistService {

	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private SongDao songDao;
	@Resource
	private UserDao userDao;
	@Resource
	private CacheService cacheService;
	@Resource
	private RedisUtil redisUtil;

	//定义Redis中有关歌单的键名静态常量
	private static final String SONG = "song"; //歌单缓存<歌曲ID，歌曲对象>
	private static final String PLAYLIST = "playlist"; //歌单缓存<歌单ID，歌单对象>
	private static final String PLAYLIST_SONGS = "playlist_songs";//歌单_歌曲缓存<歌单ID，歌单中的歌曲列表>
	private static final String USER_PLAYLISTS = "user_playlists";//用户_歌单缓存<用户ID，用户的歌单列表>
	
	/**
	 * 获取歌单信息
	 * @param id 歌单ID
	 * @return 返回歌单对象
	 */
	@Override
	public Playlist getInfo(int id) {
		Playlist playlist = cacheService.getAndCachePlaylistByPlaylistID(id);
		return playlist;
	}

	/**
	 * 创建歌单
	 * @param userId 用户ID
	 * @param playlist 歌单信息
	 * @return 返回新建的歌单ID
	 */
	@Override
	public int create(String userId, Playlist playlist) {
		//对字符串类型的属性进行预处理
		playlist.setName(playlist.getName().trim());
		playlist.setIntro(playlist.getIntro().trim());
		
		playlistDao.insert(userId,playlist);
		//新增歌单存入歌单缓存，用户_歌单缓存
		redisUtil.hset(PLAYLIST, String.valueOf(playlist.getId()), playlist, TimeConstant.A_DAY);
//		redisUtil.hset(USER_PLAYLISTS, userId, playlist, TimeConstant.A_DAY);
		
		Object object = redisUtil.hget(USER_PLAYLISTS, userId);
		if(object == null) {
			List<Playlist> playlistList = getPlaylistList(userId);
			if(playlistList == null)
				playlistList = new ArrayList<>();
			playlistList.add(playlist);
			redisUtil.hset(USER_PLAYLISTS, userId, playlistList, TimeConstant.A_DAY);
		} else {
			@SuppressWarnings("unchecked")
			List<Playlist> playlistList = (List<Playlist>)object;
			playlistList.add(playlist);
			redisUtil.hset(USER_PLAYLISTS, userId, playlistList, TimeConstant.A_DAY);
		}
		
		return playlist.getId();
	}

	/**
	 * 设置歌单图片
	 * @param d 歌单ID
	 * @param image 上传的图片在服务器的路径
	 * @return 设置歌单图片状态
	 */
	@Override
	public boolean setImage(int id, String image) {
		image.trim();
		String imageOld = cacheService.getAndCachePlaylistByPlaylistID(id).getImage();
		//该数据库
		playlistDao.updateImage(id, image);
		//改缓存
		redisUtil.hdel(PLAYLIST, String.valueOf(id));
		cacheService.getAndCachePlaylistByPlaylistID(id);
		redisUtil.del(USER_PLAYLISTS);
		//新上传的歌单图片跟旧歌单图片不重名则删除文件夹中的图片文件
		if(!image.equals(imageOld)) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			String playlistImageFilePath = WebInfoPath + imageOld;
			FileUtil.deleteFile(new File(playlistImageFilePath));
		}
		return true;
	}

	/**
	 * 修改歌单信息
	 * @param Playlist 存有修改的歌单信息的歌单
	 * @return 修改歌单信息状态
	 */
	@Override
	public boolean modifyInfo(Playlist playlist) {
		playlist.setName(playlist.getName().trim());
		playlist.setImage(playlist.getImage().trim());
		playlist.setIntro(playlist.getIntro().trim());
		
		playlistDao.update(playlist);
		redisUtil.hset(PLAYLIST, String.valueOf(playlist.getId()), playlist, TimeConstant.A_DAY);
		redisUtil.del(USER_PLAYLISTS);
		return true;
	}

	/**
	 * 为歌单新增歌曲
	 * @param songId 新增的歌曲ID
	 * @param playlistId 歌单ID
	 * @return 新增歌曲状态
	 */
	@Override
	public boolean addSong(int playlistId, int songId) {
		List<Song> songList = getSongList(playlistId);
		if(songList == null) songList = new ArrayList<>();
		for(Song song: songList) {
			if(song.getId() == songId)
				return true;
		}
		playlistDao.insertSong(playlistId, songId);
		Song song = cacheService.getAndCacheSongBySongID(songId);
		songList.add(song);
		//修改歌单_歌曲列表缓存
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 从歌单中删除歌曲
	 * @param playlistId 歌单ID
	 * @param songId 移除的歌单ID
	 * @return 移除歌曲状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeSong(int playlistId, int songId) {
		playlistDao.deleteSong(playlistId, songId);
		List<Song> songList;
		//先判断缓存是否有目标数据
		Object object = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(playlistId));
		//若无，从数据库取出新增歌曲后的歌曲列表
		if(object == null) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			
			songList = playlistDao.selectAllSongs(playlistId);
			for(Song song: songList) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//若有，根据新增歌曲ID取出歌曲信息，并添加到歌单的歌曲列表
		else {
			songList = (List<Song>) object;
			//找到要删除的歌曲并删除
			for(Song song: songList) {
				if(song.getId() == songId)
					songList.remove(song);
			}
			//若删除歌曲后的歌曲列表为空，则删除歌单_歌曲缓存中的对应歌单
			if(songList.size() == 0) {
				redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(playlistId));
				return true;
			}	
		}
		//存入缓存
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 向歌单从添加另一歌单所有歌曲
	 * @param fromId 被复制的歌单ID
	 * @param toId 新增歌曲的歌单ID
	 * @return 向歌单从添加另一歌单所有歌曲状态
	 */
	@Override
	public boolean addPlaylistToPlaylist(int fromId, int toId) {
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(toId));
		playlistDao.insertPlaylistToPlaylist(fromId, toId);
		List<Song> songList = getSongList(toId);
		for(Song song: songList) {
			if(redisUtil.hget(SONG, String.valueOf(song.getId())) == null)
				redisUtil.hset(SONG, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
		}
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(toId), songList);
		return true;
	}

	/**
	 * 向歌单中添加专辑
	 * @param albumId 专辑ID
	 * @param playlistId 歌单ID
	 * @return 向歌单添加专辑状态
	 */
	@Override
	public boolean addAlbumToPlaylist(int albumId, int playlistId) {
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(playlistId));
		playlistDao.insertAlbumToPlaylist(albumId, playlistId);
		List<Song> songList = getSongList(playlistId);
		for(Song song: songList) {
			if(redisUtil.hget(SONG, String.valueOf(song.getId())) == null)
				redisUtil.hset(SONG, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
		}
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList);
		return true;
	}

	/**
	 * 删除歌单
	 * @param id 歌单ID
	 * @return 删除歌单的状态
	 */
	@Override
	public boolean remove(int id) {
		playlistDao.delete(id);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(id));
		redisUtil.hdel(PLAYLIST, String.valueOf(id));
		redisUtil.del(USER_PLAYLISTS);
		return false;
	}

	/**
	 * 获取歌单的歌曲列表
	 * @param playlistId 歌单ID
	 * @return 歌单的歌曲列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Song> getSongList(int playlistId) {
		Object object = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(playlistId));
		List<Song> songList;
		if(object == null) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));

			songList = playlistDao.selectAllSongs(playlistId);
			if(songList == null) songList = new ArrayList<>();
			System.out.println(songList.size());
			for(Song song: songList) {
				if(song.getImage() == null) {
					String albumImage = cacheService.getAndCacheAlbumByAlbumID(songList.get(0).getAlbumId()).getImage();
					song.setImage(albumImage);
				}
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
			if(songList != null) {
				redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
			}
		}
		else songList = (List<Song>) object;
		return songList;
	}
	
	/**
	 * 显示用户的歌单列表
	 * @param userId 获取用户ID
	 * @return 歌单列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Playlist> getPlaylistList(String userId){
		userId.trim();
		Object object = redisUtil.hget(USER_PLAYLISTS, userId);
		if(object == null) {
			List<Playlist> playlistList = userDao.selectPlaylists(userId);
			if(playlistList != null) {
				redisUtil.hset(USER_PLAYLISTS, userId, playlistList, TimeConstant.A_DAY);
			}
			return playlistList;
		}
		return (List<Playlist>) object;
	}

	/**
	 * 验证用户是否拥有对应歌单
	 * @param userId 用户ID
	 * @param playlistId 歌单ID
	 * @return 是否拥有的状态
	 */
	@Override
	public Boolean checkPossession(String userId, int playlistId) {
		List<Playlist> playlistList = getPlaylistList(userId);
		for(Playlist p: playlistList) {
			if(p.getId() == playlistId) {
				return true;
			}
		}
		return false;
	}

}
