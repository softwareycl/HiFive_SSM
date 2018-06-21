package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;

public interface ArtistService {
	// 搜索
	List<Artist> search(String name, int page);

	List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page);
	
	int getFilterCount(String initial, int region, int gender);

	Artist getInfo(int id);

	int add(Artist artist);

	boolean remove(int id);

	boolean modify(Artist artist);

	boolean setImage(int id, String image);

	List<Song> lookUpSongsByArtist(int id);

	List<Album> lookUpAlbumsByArtist(int id);

	int getSearchCount(String name);
	
}
