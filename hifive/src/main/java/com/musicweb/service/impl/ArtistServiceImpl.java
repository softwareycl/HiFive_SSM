package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.ArtistDao;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.ArtistService;

@Service("artistService")
public class ArtistServiceImpl implements ArtistService {
	
	@Resource
	private ArtistDao artistDao;

	@Override
	public List<Artist> search(String name, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Artist getInfo(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int add(Artist artist) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modify(Artist artist) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setImage(int id, String image) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Song> lookUpSongsByArtist(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> lookUpAlbumsByArtist(int id) {
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
	public int getFilterCount(String initial, int region, int gender) {
		// TODO Auto-generated method stub
		return 0;
	}

}
