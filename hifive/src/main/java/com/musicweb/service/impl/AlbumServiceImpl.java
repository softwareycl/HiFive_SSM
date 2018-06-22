package com.musicweb.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.service.AlbumService;
import com.musicweb.service.CacheService;
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
	
	private String redisAlbumFilter = "album_filter";
	private String redisAlbumFilterCount = "album_filter_count";
	private String redisAlbumSongs = "album_songs";
	private String redisSong = "song";
	private String redisAlbum = "album";
	private String redisPlaylist = "playlist";
	private String redisArtistAlbums = "artist_albums";
	private String redisUserAlbums = "user_albums";
	private String redisUserSongs = "user_songs";
	private String redisPlaylistSongs = "playlist_songs";
	
	private String redisRank = "rank";
	private String redisNewSong = "new_song";
	private String redisNewAlbum = "new_album";
	private String redisSongPlayCount = "song_play_count";
	private String redisAlbumPlayCount = "album_play_count";
	
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

	@Override
	public Album getInfo(int id) {
		return cacheService.getAndCacheAlbumByAlbumID(id);
	}

	@Override
	public List<Song> getSongList(int id) {
		String redisKey = String.valueOf(id);
		Object object = redisUtil.hget(redisAlbumSongs, redisKey);
		if(object == null) {
			List<Song> songList = albumDao.selectAllSongs(id);
			if(songList != null) {
				for(Song song: songList) {
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

	@Override
	public List<Album> lookUpAlbumsByCatagory(int region, int style, int page) {
		String redisKey = region + "_" + style + "_" + page;
		Object object = redisUtil.hget(redisAlbumFilter, redisKey);
		if(object == null) {
			int offset = DisplayConstant.ALBUM_PAGE_ALBUM_SIZE * (page - 1);
			int count = DisplayConstant.ALBUM_PAGE_ALBUM_SIZE;
			List<Album> albumList = albumDao.selectByCategory(region, style, offset, count);
			if(albumList == null) return null;
 
			redisUtil.hset(redisAlbumFilter, redisKey, albumList, TimeConstant.A_DAY);
			return albumList;
		} else {
			@SuppressWarnings("unchecked")
			ArrayList<Album> albumList = (ArrayList<Album>)object;
			return albumList;
		}
	}

	@Override
	public int add(Album album) {
		int id = albumDao.insert(album);
		//补全album属性
		album.setId(id);
		album.setPlayCount(0);
		Artist artist = cacheService.getAndCacheSingerBySingerID(album.getArtistId());
		album.setArtistName(artist.getName());
		//放入缓存
		redisUtil.hset(redisAlbum, String.valueOf(id), album, TimeConstant.A_DAY);
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
		//playCount缓存
		redisUtil.hset(redisArtistAlbums, String.valueOf(album.getArtistId()), 0);
		
		return id;
	}

	@Override
	public boolean remove(int id) {
		Album album = cacheService.getAndCacheAlbumByAlbumID(id);
		
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfPath = classPath.substring(0, classPath.indexOf(FileUtil.FILE_SEPARATOR + "classes"));
		//删除专辑图片
		String albumImageFilePath = WebInfPath + album.getImage();
		FileUtil.deleteFile(new File(albumImageFilePath));
		
		//获取歌曲列表
		List<Song> songList = getSongList(id);
		if (songList != null) {
			//删除歌曲图片
			Song firstSong = songList.get(0);
			String songImageFolderPath = WebInfPath + "/image/song/" + firstSong.getArtistName() + "/" + firstSong.getAlbumName();
			FileUtil.deleteFolder(new File(songImageFolderPath));
			//删除歌词
			String lyricsPath = songList.get(0).getLyricsPath();
			String lyricsFolderPath = WebInfPath + lyricsPath.substring(0, lyricsPath.lastIndexOf('/'));
			FileUtil.deleteFolder(new File(lyricsFolderPath));
			//删除音乐文件
			String musicPath = songList.get(0).getFilePath();
			String musicFolderPath = WebInfPath + musicPath.substring(0, musicPath.lastIndexOf('/'));
			FileUtil.deleteFolder(new File(musicFolderPath));
			
			for(Song song: songList) {
				int songId = song.getId();
				//删除歌曲缓存
				redisUtil.hdel(redisSong, String.valueOf(songId));
				//删除歌曲数据库
				songDao.delete(songId);
				//删除歌曲playcount
				redisUtil.hdel(redisSongPlayCount, songId);
	
				//删除数据库歌单中的歌
				playlistDao.deleteSongInAll(songId);
				//删除缓存歌单中的歌
				redisUtil.del(redisPlaylistSongs);
				//删除数据库用户喜欢的歌
				userDao.deleteLikeSongInAll(songId);
				//删除数据库用户喜欢的歌
				redisUtil.del(redisUserSongs);
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
		
		//删除专辑歌曲关系缓存
		redisUtil.hdel(redisAlbumSongs, String.valueOf(id));
		//删除歌手专辑关系缓存
		redisUtil.hdel(redisArtistAlbums, String.valueOf(album.getArtistId()));
		
		return true;
	}

	@Override
	public boolean modify(Album album) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setImage(int id, String image) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Album> lookUpNewAlbums(int region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSearchCount(String name) {
		return albumDao.selectCountByName(name);
	}

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
