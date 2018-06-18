package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;

public interface PlaylistService {
	Playlist getInfo(int id);

	int create(Playlist playlist);

	boolean setImage(int id, String image);

	boolean modifyInfo(Playlist playlist);

	boolean remove(int id);

	List<Song> getSongList(int playlistId);

	boolean addSong(int playlistId, int songId);

	boolean removeSong(int playlistId, int songId);

	boolean addPlaylistToPlaylist(int fromId, int toId);

	boolean addAlbumToPlaylist(int albumId, int playlistId);
	
	//新增
	int getSongCount(int playlistId);
	
}
