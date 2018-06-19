package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.SongDao;
import com.musicweb.domain.Song;
import com.musicweb.service.SongService;

@Service("songService")
public class SongServiceImpl implements SongService {
	
	@Resource
	private SongDao songDao;

	@Override
	public List<Song> search(String songName, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Song> lookUpRank(int type, boolean isAll) {
		// 根据type选择不同的dao方法
		return null;
	}

	@Override
	public Song getInfo(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void play(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int add(Song song) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modify(Song song) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setImage(int id, String image) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Song> lookUpNewSongs(int region) {
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
	public String getDuration(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setLyricsPath(int id, String lyricsPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setFilePath(int id, String filePath) {
		// TODO Auto-generated method stub
		return false;
	}

}
