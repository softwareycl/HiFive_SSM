package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.service.PlaylistService;

@Service("playlistService")
public class PlaylistServiceImpl implements PlaylistService {
	
	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private AlbumDao albumDao;

	@Override
	public Playlist getInfo(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Playlist playlist) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setImage(int id, String image) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyInfo(Playlist playlist) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addSong(int playlistId, int songId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeSong(int playlistId, int songId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPlaylistToPlaylist(int fromId, int toId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAlbumToPlaylist(int albumId, int playlistId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSongCount(int playlistId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Song> getSongList(int playlistId) {
		// TODO Auto-generated method stub
		return null;
	}

}
