package com.musicweb.service;

import com.musicweb.domain.User;
import com.musicweb.view.AlbumView;
import com.musicweb.view.ArtistView;
import com.musicweb.view.SongView;
import com.musicweb.view.PlaylistView;

public interface CacheService {
	ArtistView getAndCacheSingerBySingerID(int singerID);

	AlbumView getAndCacheAlbumByAlbumID(int albumID);

	SongView getAndCacheSongBySongID(int songID);

	PlaylistView getAndCachePlaylistByPlaylistID(int playlistID);
	
	User getAndCacheUserByUserID(int userID);
}