package com.musicweb.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.service.AlbumService;
import com.musicweb.service.CacheService;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.FileUtil;
import com.musicweb.util.RedisUtil;
import com.musicweb.constant.DisplayConstant;
import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.dao.UserDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;

/**
 * ArtistServiceImpl
 * AlbumServiceImpl完成有关歌手模块的业务逻辑实现<br/>
 * 接受AlbumController的调用，通过对Dao层各类方法的调用，完成业务逻辑<br/>
 * 操作完成后，将操作结果返回给AlbumController<br/>
 * Service层针对业务数据增加各类缓存操作
 * 
 * @author zhanghuakui
 * @Date 2018.6.23
 */
@Service("albumService")
public class AlbumServiceImpl implements AlbumService {
	
	@Resource
	private AlbumDao albumDao;
	@Resource
	private SongDao songDao;
	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private UserDao userDao;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private CacheService cacheService;
	@Resource
	private RedisUtil redisUtil;
	
	//redis中的key
	private String redisAlbumFilter = "album_filter";
	private String redisAlbumFilterCount = "album_filter_count";
	private String redisAlbumSongs = "album_songs";
	private String redisSong = "song";
	private String redisAlbum = "album";
	private String redisArtistAlbums = "artist_albums";
	private String redisUserAlbums = "user_albums";
	private String redisUserSongs = "user_songs";
	private String redisPlaylistSongs = "playlist_songs";
	
	private String redisRank = "rank";
	private String redisNewSong = "new_song";
	private String redisNewAlbum = "new_album";
	private String redisSongPlayCount = "song_play_count";
	private String redisAlbumPlayCount = "album_play_count";
	
	/**
	 * 以名字为关键字搜索歌手
	 * 
	 * @param name 歌手名字
	 * @param page 目标页码
	 * @return List<Album> 歌手列表
	 */
	@Override
	public List<Album> search(String name, int page) {
		int offset = DisplayConstant.SEARCH_PAGE_ALBUM_SIZE * (page - 1);
		int count = DisplayConstant.SEARCH_PAGE_ALBUM_SIZE;
		List<Album> albumList = albumDao.selectByName(name, offset, count);
		if(albumList != null)
			for(Album album: albumList) {
				redisUtil.hset(redisAlbum, String.valueOf(album.getId()), album, TimeConstant.A_DAY);
			}
		return albumList;
	}

	/**
	 * 显示歌手详情
	 * 
	 * @param id 专辑id
	 * @return 专辑 Album
	 */
	@Override
	public Album getInfo(int id) {
		return cacheService.getAndCacheAlbumByAlbumID(id);
	}

	/**
	 * 查看专辑的歌曲列表
	 * 
	 * @param id 专辑id
	 * @return 歌曲列表
	 */
	@Override
	public List<Song> getSongList(int id) {
		String redisKey = String.valueOf(id);
		Object object = redisUtil.hget(redisAlbumSongs, redisKey);
		if(object == null) {
			Album album = cacheService.getAndCacheAlbumByAlbumID(id);
			List<Song> songList = albumDao.selectAllSongs(id);
			if(songList != null) {
				String classPath = this.getClass().getClassLoader().getResource("").getPath();
				String WebInfPath = classPath.substring(0, classPath.indexOf("/classes"));
				for(Song song: songList) {
					if(song.getImage() == null) {
						song.setImage(album.getImage());
					}
					String musicFilePath = WebInfPath + song.getFilePath();
					song.setDuration(DurationUtil.computeDuration(musicFilePath));
					redisUtil.hset(redisSong, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
				}
			}
			redisUtil.hset(redisAlbumSongs, redisKey, songList, TimeConstant.A_DAY);
			return songList;
		} else {
			@SuppressWarnings("unchecked")
			ArrayList<Song> songList = (ArrayList<Song>)object;
			return songList;
		}
	}

	/**
	 * 根据地区，更风格类别筛选歌手
	 * 
	 * @param region 地区
	 * @param style 风格
	 * @param page 目标页码
	 * @return 专辑列表
	 */
	@Override
	public List<Album> lookUpAlbumsByCatagory(int region, int style, int page) {
		String redisKey = region + "_" + style + "_" + page;
		Object object = redisUtil.hget(redisAlbumFilter, redisKey);
		if(object == null) {
			int offset = DisplayConstant.ALBUM_PAGE_ALBUM_SIZE * (page - 1);
			int count = DisplayConstant.ALBUM_PAGE_ALBUM_SIZE;
			List<Album> albumList = albumDao.selectByCategory(region, style, offset, count);
			if(albumList == null) return null;
			for(Album album: albumList) {
				redisUtil.hset(redisAlbum, String.valueOf(album.getId()), album);
			}
			
			redisUtil.hset(redisAlbumFilter, redisKey, albumList, TimeConstant.A_DAY);
			return albumList;
		} else {
			@SuppressWarnings("unchecked")
			ArrayList<Album> albumList = (ArrayList<Album>)object;
			return albumList;
		}
	}

	/**
	 * 添加专辑
	 * 
	 * @param album 专辑
	 * @return 新增的专辑ID
	 */
	@Override
	public int add(Album album) {	
		//补全album属性
		album.setPlayCount(0);
		Artist artist = cacheService.getAndCacheSingerBySingerID(album.getArtistId());
		album.setArtistName(artist.getName());
		//插入数据库
		albumDao.insert(album);
		if (album.getId() == 0) {
			return 0;
		}
		//放入缓存
		redisUtil.hset(redisAlbum, String.valueOf(album.getId()), album, TimeConstant.A_DAY);
		//更改相关缓存
		Object object = redisUtil.hget(redisArtistAlbums, String.valueOf(album.getArtistId()));
		if(object != null) {
			@SuppressWarnings("unchecked")
			List<Album> albumList = (List<Album>)object;
			albumList.add(0, album);
			redisUtil.hset(redisArtistAlbums, String.valueOf(album.getArtistId()), albumList, TimeConstant.A_DAY);
		}
		redisUtil.del(redisAlbumFilter);
		redisUtil.del(redisAlbumFilterCount);
		//playCount缓存（不是redisArtistAlbums）
		redisUtil.hset(redisAlbumPlayCount, String.valueOf(album.getId()), 0);
		
		return album.getId();
	}

	/**
	 * 删除专辑
	 * 
	 * @param id 专辑ID
	 * @return 操作状态
	 */
	@Override
	public boolean remove(int id) {
		Album album = cacheService.getAndCacheAlbumByAlbumID(id);
		if(album == null) return true;
		
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfPath = classPath.substring(0, classPath.indexOf("/classes"));
		
		//删除专辑图片
		if(album.getImage() != null) {
			String albumImageFilePath = WebInfPath + album.getImage();
			FileUtil.deleteFile(new File(albumImageFilePath));
		}
		
		//获取歌曲列表
		List<Song> songList = getSongList(id);
		if (songList != null && songList.size()!=0) {
			//删除歌曲图片
			Song firstSong = songList.get(0);
			String songImageFolderPath = WebInfPath + "/image/song/" + firstSong.getArtistName() + "/" + firstSong.getAlbumName();
			FileUtil.deleteFolder(new File(songImageFolderPath));
			//删除歌词
			String lyricsFolderPath = WebInfPath + "/lyrics/" + firstSong.getArtistName() + "/" + firstSong.getAlbumName();
			FileUtil.deleteFolder(new File(lyricsFolderPath));
			//删除音乐文件
			String musicFolderPath = WebInfPath + "/music/" + firstSong.getArtistName() + "/" + firstSong.getAlbumName();
			FileUtil.deleteFolder(new File(musicFolderPath));
			
			for(Song song: songList) {
				int songId = song.getId();
				//删除歌曲缓存
				redisUtil.hdel(redisSong, String.valueOf(songId));
				//删除歌曲数据库
				songDao.delete(songId);
				//删除歌曲playcount
				redisUtil.hdel(redisSongPlayCount, String.valueOf(songId));
	
				//删除数据库歌单中的歌
				playlistDao.deleteSongInAll(songId);
				
				//删除数据库用户喜欢的歌
				userDao.deleteLikeSongInAll(songId);
			}
		}
		
		//删除album缓存
		redisUtil.hdel(redisAlbum, String.valueOf(id));
		//删除album数据库
		albumDao.delete(id);
		//删除专辑playcount
		redisUtil.hdel(redisAlbumPlayCount, String.valueOf(album.getId()));
		
		//删除筛选专辑缓存
		redisUtil.del(redisAlbumFilter);
		redisUtil.del(redisAlbumFilterCount);
		
		//删除排行榜、新歌、新专辑
		redisUtil.del(redisRank);
		redisUtil.del(redisNewSong);
		redisUtil.del(redisNewAlbum);
		
		//删除数据库用户喜欢的专辑
		userDao.deleteLikeAlbumInAll(id);
		//删除缓存用户喜欢的专辑
		redisUtil.del(redisUserAlbums);
		//删除数据库用户喜欢的歌
		redisUtil.del(redisUserSongs);
		
		//删除缓存歌单中的歌
		redisUtil.del(redisPlaylistSongs);
		
		//删除专辑歌曲关系缓存
		redisUtil.hdel(redisAlbumSongs, String.valueOf(id));
		//删除歌手专辑关系缓存
		redisUtil.hdel(redisArtistAlbums, String.valueOf(album.getArtistId()));
		
		return true;
	}

	/**
	 * 修改专辑信息
	 * 
	 * @param album 专辑
	 * @return 操作状态
	 */
	@Override
	public boolean modify(Album album) {
		Album oldAlbum = cacheService.getAndCacheAlbumByAlbumID(album.getId());
		
		//删除新专辑
		redisUtil.del(redisNewAlbum);
		//删除缓存用户喜欢的专辑
		redisUtil.del(redisUserAlbums);
		
		//如果专辑名称被修改了，那么缓存中这张专辑的所有歌曲都需要修改，因为歌曲里有属性表示专辑名称
		if(!oldAlbum.getName().equals(album.getName())) {
			//修改对应song和album_songs缓存
			List<Song> songList = getSongList(album.getId());
			for(Song song: songList) {
				song.setAlbumName(album.getName());
				redisUtil.hset(redisSong, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
			}
			redisUtil.hset(redisAlbumSongs, String.valueOf(album.getId()), songList, TimeConstant.A_DAY);
		}
		
		//补全album属性
		album.setPlayCount(oldAlbum.getPlayCount());
		album.setImage(oldAlbum.getImage());
		Artist artist = cacheService.getAndCacheSingerBySingerID(oldAlbum.getArtistId());
		album.setArtistId(oldAlbum.getArtistId());
		album.setArtistName(artist.getName());
		
		//修改数据库
		albumDao.update(album);
		//放入缓存，其实是更新了缓存
		redisUtil.hset(redisAlbum, String.valueOf(album.getId()), album, TimeConstant.A_DAY);
		
		//删除筛选专辑缓存
		redisUtil.del(redisAlbumFilter);
		redisUtil.del(redisAlbumFilterCount);
		
		//删除排行榜、新歌
		redisUtil.del(redisRank);
		redisUtil.del(redisNewSong);
		
		//删除新专辑
		redisUtil.del(redisNewAlbum);
		//删除缓存用户喜欢的专辑
		redisUtil.del(redisUserAlbums);
		
		//删除数据库用户喜欢的歌
		redisUtil.del(redisUserSongs);
		//删除缓存歌单中的歌
		redisUtil.del(redisPlaylistSongs);
		
		return true;
	}

	/**
	 * 设置专辑图片。如果数据库中歌曲没有图片，那么缓存中歌曲图片需要根据专辑图片的修改而修改；<br/>如果数据库中歌曲有图片，那么缓存中歌曲图片 不 需要根据专辑图片的修改而修改。
	 * 
	 * @param id 专辑id
	 * @param image 专辑图片路径
	 * @return true表示成功，false表示失败
	 */
	@Override
	public boolean setImage(int id, String image) {
		Album album = cacheService.getAndCacheAlbumByAlbumID(id);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfPath = classPath.substring(0, classPath.indexOf("/classes"));
		//删除旧图片
		FileUtil.deleteFile(new File(WebInfPath + album.getImage()));
		album.setImage(image);
		//更新数据库
		albumDao.updateImage(id, image);
		redisUtil.hset(redisAlbum, String.valueOf(id), album);
		//删除对应artist_albums缓存
		redisUtil.hdel(redisArtistAlbums, String.valueOf(album.getArtistId()));
		
		//删除筛选专辑缓存
		redisUtil.del(redisAlbumFilter);
		redisUtil.del(redisAlbumFilterCount);
		
		//删除排行榜、新歌
		redisUtil.del(redisRank);
		redisUtil.del(redisNewSong);
		
		//删除数据库用户喜欢的歌
		redisUtil.del(redisUserSongs);
		//删除缓存歌单中的歌
		redisUtil.del(redisPlaylistSongs);
		
		//修改对应song和album_songs缓存
		List<Song> songList = getSongList(album.getId());
		for(Song song: songList) {
			song.setAlbumName(album.getName());
			redisUtil.hset(redisSong, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
			if (songDao.selectById(song.getId()).getImage() == null) {//如果数据库中歌曲没有图片的话，那缓存中歌曲的图片也需要改
				song.setImage(image);
				redisUtil.hset(redisSong, String.valueOf(song.getId()), song, TimeConstant.A_DAY);
			}
		}
		redisUtil.hset(redisAlbumSongs, String.valueOf(album.getId()), songList, TimeConstant.A_DAY);
				
		return false;
	}

	/**
	 * 获取最新发布的专辑
	 * 
	 * @param region 地区
	 * @return 专辑列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Album> lookUpNewAlbums(int region) {
		Object object = redisUtil.hget(redisNewAlbum, String.valueOf(region));
		if(object == null) {
			List<Album> albumList = albumDao.selectLatest(region, DisplayConstant.HOME_PAGE_NEW_ALBUM_SIZE);
			redisUtil.hset(redisNewAlbum, String.valueOf(region), albumList);
			return albumList;
		} else {
			return (List<Album>)object;
		}
	}

	/**
	 * 获取搜索结果的记录条数
	 * 
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	@Override
	public int getSearchCount(String name) {
		return albumDao.selectCountByName(name);
	}

	/**
	 * 获取筛选后的歌手数目
	 * 
	 * @param region 地区
	 * @param style 风格
	 * @return 专辑数目
	 */
	@Override
	public int getFilterCount(int region, int style) {
		String redisKey = region + "_" + style;
		Object object = redisUtil.hget(redisAlbumFilterCount, redisKey);
		if(object == null) {
			int count = albumDao.selectCountByCategory(region, style);
			redisUtil.hset(redisAlbumFilterCount, redisKey, count, TimeConstant.A_DAY);
			return count;
		} else {
			return (int)object;
		}
	}
}
