package com.musicweb.service;

import java.util.*;

import com.musicweb.domain.Album;
import com.musicweb.domain.Song;

public interface AlbumService {
	// 搜索
	List<Album> search(String name, int page);

	Album getInfo(int id);

	List<Song> getSongList(int id);

	List<Album> lookUpAlbumsByCatagory(int region, int style, int page);
	
	int getFilterCount(int region, int style);

	int add(Album album);

	boolean remove(int id);

	boolean modify(Album album);

	boolean setImage(int id, String image);

	List<Album> lookUpNewAlbums(int region);

	int getSearchCount(String name);
	
}
