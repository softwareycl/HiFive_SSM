package com.musicweb.service.impl;

import org.springframework.stereotype.Service;

import com.musicweb.domain.User;
import com.musicweb.service.CacheService;
import com.musicweb.view.AlbumView;
import com.musicweb.view.ArtistView;
import com.musicweb.view.PlaylistView;
import com.musicweb.view.SongView;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

	@Override
	public ArtistView getAndCacheSingerBySingerID(int singerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlbumView getAndCacheAlbumByAlbumID(int albumID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SongView getAndCacheSongBySongID(int songID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlaylistView getAndCachePlaylistByPlaylistID(int playlistID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getAndCacheUserByUserID(int userID) {
		// TODO Auto-generated method stub
		return null;
	}


}
