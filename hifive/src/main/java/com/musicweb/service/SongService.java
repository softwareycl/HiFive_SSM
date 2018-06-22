package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Song;

/**
 * 歌曲模块业务逻辑接口
 * 
 * @author brian
 *
 */
public interface SongService {
	/**
	 * 搜索歌曲
	 * 
	 * @param name 输入的歌曲名称，或歌曲名称的一部分
	 * @param page 目标页号
	 * @return 歌曲列表
	 */
	List<Song> search(String name, int page);
	
	/**
	 * 返回歌曲搜索结果的数目
	 * 
	 * @param name 输入的歌曲名称，或歌曲名称的一部分
	 * @return 歌曲搜索结果的数目
	 */
	int getSearchCount(String name);

	/**
	 * 获取歌曲排行榜
	 * 
	 * @param type 排行榜类型
	 * @param isAll 排行榜的位置？？？
	 * @return 歌曲列表
	 */
	List<Song> lookUpRank(int type, boolean isAll);

	/**
	 * 获取歌曲详情
	 * 
	 * @param id 歌曲id
	 * @return 歌曲详情
	 */
	Song getInfo(int id);

	/**
	 * 播放歌曲，增加该歌曲播放量
	 * 
	 * @param id 歌曲id
	 */
	void play(int id);

	/**
	 * 添加歌曲
	 * 
	 * @param song 将被添加的歌曲
	 * @return 新添加的歌曲id
	 */
	int add(Song song);

	/**
	 * 删除歌曲
	 * 
	 * @param id 将被删除的歌曲id
	 * @return 布尔值，表示成功或失败
	 */
	boolean remove(int id);

	/**
	 * 编辑歌曲
	 * 
	 * @param song 所修改的歌曲内容
	 * @return 布尔值，表示成功或失败
	 */
	boolean modify(Song song);

	/**
	 * 设置歌曲图片
	 * 
	 * @param id 歌曲id
	 * @param image 歌曲图片的路径
	 * @return 布尔值，表示成功或失败
	 */
	boolean setImage(int id, String image);	
	
	/**
	 * 设置歌词路径
	 * 
	 * @param id 歌曲id
	 * @param lyricsPath 歌词的路径
	 * @return 布尔值，表示成功或失败
	 */
	boolean setLyricsPath(int id, String lyricsPath);
	
	/**
	 * 设置歌曲音频文件的路径
	 * 
	 * @param id 歌曲id
	 * @param filePath 歌曲音频文件的路径
	 * @return 布尔值，表示成功或失败
	 */
	boolean setFilePath(int id, String filePath);

	/**
	 * 获取新歌首发
	 * 
	 * @param region 地区
	 * @return 歌曲列表
	 */
	List<Song> lookUpNewSongs(int region);
	
	/**
	 * 获取歌曲市场
	 * @param id 歌曲id
	 * @return 表示时长的字符串
	 */
	String getDuration(int id);
	
}
