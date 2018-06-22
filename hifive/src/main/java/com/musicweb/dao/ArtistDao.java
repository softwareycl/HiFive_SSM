package com.musicweb.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;

import java.util.List;

public interface ArtistDao {
	// 增
	public int insert(Artist artist);//返回id

	// 删
	public int delete(int id);

	// 改
	public int update(Artist artist);

	public int updateImage(@Param("id")int id,@Param("image") String image);
	
	public int updatePlayCount(@Param("id")int id, @Param("playCount")int playCount);

	// 查
	public Artist select(int id);

	public List<Artist> selectByName(@Param("name")String name, @Param("offset")int offset, @Param("count")int count);//按播放量排序
	
	public int selectCountByName(String name);

	public List<Artist> selectByCategory(@Param("initial")String initial, @Param("region")int region, @Param("gender")int gender, @Param("offset")int offset, @Param("count")int count);//按播放量排序
	
	public int selectCountByCategory(@Param("initial")String initial, @Param("region")int region, @Param("gender")int gender);
	
	public List<Song> selectAllSongs(int artistId);//按播放量排序
	
	public List<Album> selectAllAlbums(int artistId);//按播放量排序

}
