package com.musicweb.dao;

import java.util.List;

import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;

public interface PlaylistDao {
	//增
	public int insert(Playlist playlist);// 返回id
	
	public int insertSong(int playlistId, int songId);
	
	public boolean insertPlaylistToPlaylist(int fromId, int toId);

	public boolean insertAlbumToPlaylist(int albumId, int playlistId);//暂时保留，需不需要迁移到service完成
	
	//删
	public int delete(int id);
	
	public int deleteSong(int playlistId, int songId);
	
	//改
	public int update(Playlist playlist);

	public int updateImage(int id, String image);
	
	//查
	public Playlist select(int id);
	
	public int selectSongCount(int playlistId);
	
	public List<Song> selectAllSongs(int playlistId);

}
