package com.musicweb.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.musicweb.domain.Album;
import com.musicweb.domain.Song;

import java.util.Date;
import java.util.List;

public interface SongDao {
	// 增
	public int insert(Song song);// 返回歌曲ID
	
	// 改
	public int updateLyricsPath(@Param("id")int id, @Param("lyricsPath")String lyricsPath);

	public int updateImage(@Param("id")int id, @Param("image")String image);
	
	public int updateFilePath(@Param("id")int id, @Param("filePath")String filePath);

	public int updatePlayCount(@Param("id")int id, @Param("playCount")int playCount);

	public int update(Song song);

	// 删
	public int delete(int id);

	// 查
	public Song selectById(int id);

	public List<Song> selectByName(@Param("name")String name, @Param("offset")int offset, @Param("count")int count);//按播放量排序
	
	public int selectCountByName(String name);
	
	public List<Song> selectLatest(@Param("region")int region, @Param("count")int count);//先按发行时间排序选择count首歌曲再按播放量排序
	
	public List<Song> selectHittest(int count);//按播放量排序
	
	public List<Song> selectRankByRegion(@Param("region")int region, @Param("count")int count);//按播放量排序

}
