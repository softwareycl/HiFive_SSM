package com.musicweb.service.impl;

import java.io.File;
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
		playlist.setImage(playlist.getImage().trim());
		playlist.setIntro(playlist.getIntro().trim());
		userId.trim();
		
		int id = playlistDao.insert(userId,playlist);
		//新增歌单存入歌单缓存，用户_歌单缓存
		redisUtil.hset(PLAYLIST, String.valueOf(playlist.getId()), playlist, TimeConstant.A_DAY);
		redisUtil.hset(USER_PLAYLISTS, userId, playlist, TimeConstant.A_DAY);
		return id;
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean addSong(int playlistId, int songId) {
		playlistDao.insertSong(playlistId, songId);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		
		List<Song> songList;
		//先判断缓存是否有目标数据
		Object object = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(playlistId));
		//若无，从数据库取出新增歌曲后的歌曲列表
		if(object == null) {
			songList = playlistDao.selectAllSongs(playlistId);
			for(Song song: songList) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//若有，根据新增歌曲ID取出歌曲信息，并添加到歌单的钢琴曲列表
		else {
			songList = (List<Song>) object;
			Song song = cacheService.getAndCacheSongBySongID(songId);
			if(song.getImage() == null) {
				song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
				songList.add(song);
		}
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean addPlaylistToPlaylist(int fromId, int toId) {
		playlistDao.insertPlaylistToPlaylist(fromId, toId);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		
		List<Song> songListFrom;
		//先判断缓存是否有被复制的歌单数据
		Object objectFrom = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(fromId));
		//若无，从数据库取出新增歌曲后的歌曲列表
		if(objectFrom == null) {
			songListFrom = playlistDao.selectAllSongs(fromId);
			for(Song song: songListFrom) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//若有，从缓存取出
		else songListFrom = (List<Song>) objectFrom;
		
		//接受新歌的歌单
		List<Song> songListTo;
		//先判断缓存是否有歌单数据
		Object objectTo = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(toId));
		//若无，从数据库取出新增歌曲后的歌曲列表
		if(objectTo == null) {
			songListTo = playlistDao.selectAllSongs(toId);
			for(Song song: songListTo) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		//若有，从缓存取出
		else songListTo = (List<Song>) objectTo;
		
		//复制歌曲
		for(Song song: songListFrom) {
			songListTo.add(song);
		}
		
		//修改歌单_歌曲列表缓存
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(toId), songListTo, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 向歌单中添加专辑
	 * @param albumId 专辑ID
	 * @param playlistId 歌单ID
	 * @return 向歌单添加专辑状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAlbumToPlaylist(int albumId, int playlistId) {
		playlistDao.insertAlbumToPlaylist(albumId, playlistId);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));

		//先判断缓存是否有歌单的目标数据
		List<Song> songList;
		Object object = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(playlistId));
		//若无，从数据库取出新增专辑后的歌曲列表
		if(object == null) {
			songList = playlistDao.selectAllSongs(playlistId);
			for(Song song: songList) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
		}
		else songList = (List<Song>) object;
		
		//根据专辑ID从缓存中取出专辑的歌曲列表，否则从数据库中取出
		List<Song> songInAlbum;
		Object objectSong = redisUtil.hget("album_songs", String.valueOf(albumId));
		if(objectSong == null) {
			songInAlbum = albumDao.selectAllSongs(albumId);
			for(Song song: songInAlbum) {
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
			}
			//存入缓存
			redisUtil.hset("album_songs", String.valueOf(albumId), songInAlbum);
		}
		else songInAlbum = (List<Song>) objectSong;
			
		//复制专辑到歌单
		for(Song song:songInAlbum) {
			songList.add(song);
		}
		
		//修改歌单_歌曲列表缓存
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
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
				redisUtil.hset(USER_PLAYLISTS, userId, playlistList);
			}
			return playlistList;
		}
		return (List<Playlist>) object;
	}

}
