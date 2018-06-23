package com.musicweb.service;

import java.util.*;

import com.musicweb.domain.Album;
import com.musicweb.domain.Song;

/**
 * ArtistService
 * @author zhanghuakui
 * @Date 2018.6.23
 * AlbumService完成有关歌手模块的业务逻辑实现
 * 接受AlbumController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给AlbumController
 * Service层针对业务数据增加各类缓存操作
 */
public interface AlbumService {
	/**
	 * 以名字为关键字搜索歌手
	 * @param name 歌手名字
	 * @param page 目标页码
	 * @return List<Album> 歌手列表
	 */
	List<Album> search(String name, int page);

	/**
	 * 显示歌手详情
	 * @param id 专辑id
	 * @return 专辑 Album
	 */
	Album getInfo(int id);

	/**
	 * 查看专辑的歌曲列表
	 * @param id 专辑id
	 * @return 歌曲列表
	 */
	List<Song> getSongList(int id);

	/**
	 * 根据地区，更风格类别筛选歌手
	 * @param region 地区
	 * @param style 风格
	 * @param page 目标页码
	 * @return 专辑列表
	 */
	List<Album> lookUpAlbumsByCatagory(int region, int style, int page);
	
	/**
	 * 获取筛选后的歌手数目
	 * @param region 地区
	 * @param style 风格
	 * @return 专辑数目
	 */
	int getFilterCount(int region, int style);

	/**
	 * 添加专辑
	 * @param album 专辑
	 * @return 新增的专辑ID
	 */
	int add(Album album);

	/**
	 * 删除专辑
	 * @param id 专辑ID
	 * @return 操作状态
	 */
	boolean remove(int id);

	/**
	 * 修改专辑信息
	 * @param album 专辑
	 * @return 操作状态
	 */
	boolean modify(Album album);

	/**
	 * 设置专辑图片
	 * @param id 首专辑id
	 * @param image 专辑图片路径
	 * @return 是否操作成功
	 */
	boolean setImage(int id, String image);

	/**
	 * 获取最新发布的专辑
	 * @param region 地区
	 * @return 专辑列表
	 */
	List<Album> lookUpNewAlbums(int region);

	/**
	 * 获取搜索结果的记录条数
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	int getSearchCount(String name);
	
}
