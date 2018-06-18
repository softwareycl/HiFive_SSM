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

	public int updateImage(int id, String image);
	
	public int updatePlayCount(int id, int playCount);

	// 查
	public Artist select(int id);

	public List<Artist> selectByName(String name, int offset, int count);//按播放量排序
	
	public int selectCountByName(String name);

	public List<Artist> selectByCategory(int region, int style, int offset, int count);//按播放量排序
	
	public int selectCountByCategory(int region, int style);
	
	public List<Song> selectAllSongs(int artistId);//按播放量排序
	
	public List<Album> selectAllAlbums(int artistId);//按播放量排序

}
