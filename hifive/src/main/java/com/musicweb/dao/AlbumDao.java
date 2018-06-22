package com.musicweb.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;

import java.util.Date;
import java.util.List;

public interface AlbumDao {
	// 增
	public int insert(Album album);// 返回专辑ID
	// 删

	public int delete(int id);

	// 改
	public int update(Album album);// 不更新歌曲

	public int updateImage(@Param("id")int id, @Param("image")String image);
	
	public int updatePlayCount(@Param("id")int id, @Param("playCount")int playCount);

	// 查
	public List<Album> selectByName(@Param("name")String name, @Param("offset")int offset, @Param("count")int count);//按播放量排序
	
	public int selectCountByName(String name);

	public Album select(int id);

	public List<Album> selectByCategory(@Param("region")int region, @Param("style")int style, @Param("offset")int offset, @Param("count")int count);//按播放量排序
	
	public int selectCountByCategory(@Param("region")int region,  @Param("style")int style);

	public List<Album> selectLatest(int count);//先按发行时间排序选择count张专辑再按播放量排序
	
	public List<Song> selectAllSongs(int albumId);
	
	public int selectSongCount(int albumId);
	
	public String selectImage(int id);

}
