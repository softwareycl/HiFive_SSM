package com.musicweb.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.CacheService;
import com.musicweb.util.RedisUtil;
import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

	@Resource
	private RedisUtil redisUtil;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private AlbumDao albumDao;
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Playlist getAndCachePlaylistByPlaylistID(int playlistID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getAndCacheUserByUserID(int userID) {
		// TODO Auto-generated method stub
		return null;
	}


}
