package com.musicweb.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.CacheService;
import com.musicweb.view.AlbumView;
import com.musicweb.view.ArtistView;
import com.musicweb.view.PlaylistView;
import com.musicweb.view.SongView;
import com.musicweb.util.RedisUtil;
import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.ArtistDao;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

	@Resource
	private RedisUtil redisUtil;
	@Resource
	private ArtistDao artistDao;
	
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
		// TODO Auto-generated method stub
		return null;
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
