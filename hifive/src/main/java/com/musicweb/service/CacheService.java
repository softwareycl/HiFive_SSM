package com.musicweb.service;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;

public interface CacheService {
	Artist getAndCacheSingerBySingerID(int singerID);

	Album getAndCacheAlbumByAlbumID(int albumID);

	Song getAndCacheSongBySongID(int songID);

	Playlist getAndCachePlaylistByPlaylistID(int playlistID);
	
	User getAndCacheUserByUserID(int userID);
}