package com.musicweb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;

public interface PlaylistDao {
	//增
	public int insert(@Param("userId")String userId, @Param("playlist")Playlist playlist);// 返回id
	
	public int insertSong(@Param("playlistId")int playlistId, @Param("songId")int songId);
	
	public boolean insertPlaylistToPlaylist(@Param("fromId")int fromId,@Param("toId") int toId);

	public boolean insertAlbumToPlaylist(@Param("albumId")int albumId, @Param("playlistId")int playlistId);//暂时保留，需不需要迁移到service完成
	
	//删
	public int delete(int id);
	
	public int deleteSong(@Param("playlistId")int playlistId, @Param("songId")int songId);
	
	public int deleteSongBySongId(int songId);
	
	//删除表中对应的所有歌曲
	public int deleteSongInAll(int id);
	
	//改
	public int update(Playlist playlist);

	public int updateImage(@Param("id")int id, @Param("image")String image);
	
	//查
	public Playlist select(@Param("id")int id);
	
	public int selectSongCount(int playlistId);
	
	public List<Song> selectAllSongs(int playlistId);

}
