package com.musicweb.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.CacheService;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.FileUtil;
import com.musicweb.util.RedisUtil;
import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.dao.UserDao;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

	@Resource
	private RedisUtil redisUtil;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private SongDao songDao;
	@Resource
	private UserDao userDao;
	
	@Override
	public Artist getAndCacheSingerBySingerID(int singerID) {
		Object object = redisUtil.hget("artist", String.valueOf(singerID));
		if(object == null) {
			Artist artist = artistDao.select(singerID);
			if(artist != null) {
				redisUtil.hset("artist", String.valueOf(singerID), artist, TimeConstant.A_DAY);
			}
			Object playCount = redisUtil.hget("artist_play_count", String.valueOf(singerID));
			if(playCount == null){
				redisUtil.hset("artist_play_count", String.valueOf(singerID), artist.getPlayCount());
			}
			return artist;
		}
		return (Artist)object;
	}

	@Override
	public Album getAndCacheAlbumByAlbumID(int albumID) {
		Object object = redisUtil.hget("album", String.valueOf(albumID));
		if(object == null) {
			Album album = albumDao.select(albumID);
			if(album != null) {
				redisUtil.hset("album", String.valueOf(albumID), album, TimeConstant.A_DAY);
			}
			Object playCount = redisUtil.hget("album_play_count", String.valueOf(albumID));
			if(playCount == null){
				redisUtil.hset("album_play_count", String.valueOf(albumID), album.getPlayCount());
			}
			return album;
		}
		return (Album)object;
	}

	@Override
	public Song getAndCacheSongBySongID(int songID) {
		//redisUtil.hdel("song", String.valueOf(songID));
		Object object = redisUtil.hget("song", String.valueOf(songID));
		if(object == null) {
			Song song = songDao.selectById(songID);
			if(song.getImage() == null) {
				song.setImage(albumDao.selectImage(song.getAlbumId()));
			}
			//拼接出歌曲音频文件的绝对路径
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = "/home/brian/下载/音乐数据库";//classPath.substring(0, classPath.indexOf("/classes"));
			String filePath = WebInfoPath + song.getFilePath();
			song.setDuration(DurationUtil.computeDuration(filePath));
			if(song != null) {
				redisUtil.hset("song", String.valueOf(songID), song, TimeConstant.A_DAY);
			}
			Object playCount = redisUtil.hget("song_play_count", String.valueOf(songID));
			if(playCount == null) {
				redisUtil.hset("song_play_count", String.valueOf(songID), song.getPlayCount());
			}
			return song;
		}
		return (Song)object;
	}

	@Override
	public Playlist getAndCachePlaylistByPlaylistID(int playlistID) {
		Object object = redisUtil.hget("playlist", String.valueOf(playlistID));
		if(object == null) {
			Playlist playlist = playlistDao.select(playlistID);
			if(playlist != null) {
				redisUtil.hset("playlist", String.valueOf(playlistID), playlist, TimeConstant.A_DAY);
			}
			return playlist;
		}
		return (Playlist)object;
	}

	@Override
	public User getAndCacheUserByUserID(String userID) {
		Object object = redisUtil.hget("singer", String.valueOf(userID));
		if(object == null) {
			User user = userDao.select(userID);
			if(user != null) {
				redisUtil.hset("user", String.valueOf(userID), user, TimeConstant.A_DAY);
			}
			return user;
		}
		return (User)object;
	}


}
