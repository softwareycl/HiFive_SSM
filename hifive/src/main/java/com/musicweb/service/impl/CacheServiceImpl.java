package com.musicweb.service.impl;

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

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

	@Override
	public Artist getAndCacheSingerBySingerID(int singerID) {
		// TODO Auto-generated method stub
		return null;
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
