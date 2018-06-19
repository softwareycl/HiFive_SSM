package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.service.AlbumService;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Song;

@Service("albumService")
public class AlbumServiceImpl implements AlbumService {
	
	@Resource
	private AlbumDao albumDao;
	@Resource
	private SongDao songDao;

	@Override
	public List<Album> search(String name, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Album getInfo(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> getSongList(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Album> lookUpAlbumsByCatagory(int region, int style, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int add(Album album) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(int id) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean refreshPlayCount(int id, int playCount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getFilterCount(int region, int style) {
		// TODO Auto-generated method stub
		return 0;
	}


}
