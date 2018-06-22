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
	public int updateLyricsPath(int id, String lyricsPath);

	public int updateImage(int id, String image);

	public int updateSongPath(int id, String songPath);
	
	public int updateFilePath(int id, String filePath);

	public int updatePlayCount(int id, int playCount);

	public int update(Song song);

	// 删
	public int delete(int id);

	// 查
	public Song selectById(int id);

	public List<Song> selectByName(String name, int offset, int count);//按播放量排序
	
	public int selectCountByName(String name);
	
	public List<Song> selectLatest(int region, int count);//先按发行时间排序选择count首歌曲再按播放量排序
	
	public List<Song> selectHittest(int count);//按播放量排序
	
	public List<Song> selectRankByRegion(int region, int count);//按播放量排序

}
