package com.musicweb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mchange.v2.encounter.StrongEqualityEncounterCounter;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;

public interface UserDao {
	//增
	public int insert(User user);//返回id
	
	public int insertLikeSong(@Param("user_id")String userId, @Param("song_id")int songId);

	public int insertLikeAlbum(@Param("user_id")String userId, @Param("album_id")int albumId);

	//删
	public int deleteLikeSong(@Param("user_id")String userId, @Param("song_id")int songId);

	public int deleteLikeAlbum(@Param("user_id")String userId, @Param("album_id")int albumId);

	
	//删除用户歌曲关系表中对应的所有歌曲
	public int deleteLikeSongInAll(int id);

	//删除用户专辑关系表中对应的所有专辑
	public int deleteLikeAlbumInAll(int id);
	
	//改
	public int update(User user);

	public int updateImage(@Param("id")String id, @Param("image")String image);
	
	public int updatePassword(@Param("id")String id, @Param("pwd")String pwd);

	
	//查
	public User select(String id);
	
	public String selectPassword(String id);// 找密码
	
	public int selectQuestion(String id);
	
	public String selectAnswer(String id);

	public List<Song> selectLikeSongs(String userId);

	public List<Album> selectLikeAlbums(String userId);
	
	public List<Playlist> selectPlaylists(String userId);


}
