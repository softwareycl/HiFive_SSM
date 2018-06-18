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
	
	public int insertLikeSong(String userId, int songId);

	public int insertLikeAlbum(String userId, int albumId);
	
	//删
	public int deleteLikeSong(String userId, int songId);

	public int deleteLikeAlbum(String userId, int albumId);
	
	//改
	public int update(User user);

	public int updateImage(String id, String image);
	
	public int updatePassword(String id, String pwd);
	
	//查
	public User select(String id);
	
	public String selectPassword(String id);// 找密码
	
	public int selectQuestion(String id);
	
	public String selectAnswer(String id);

	public List<Song> selectLikeSongs(String userId);

	public List<Album> selectLikeAlbums(String userId);
	
	public List<Playlist> selectPlaylists(String userId);

}
