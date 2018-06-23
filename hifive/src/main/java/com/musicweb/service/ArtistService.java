package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
/**
 * ArtistService
 * @author likexin
 * @Date 2018.6.21
 * ArtistService完成有关歌手模块的业务逻辑实现
 * 接受ArtistController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给ArtistController
 * Service层针对业务数据增加各类缓存操作
 */
public interface ArtistService {
	/**
	 * 以名字为关键字搜索歌手
	 * @param name 歌手名字
	 * @param page 目标页码
	 * @return List<Artist> 歌手列表
	 */
	List<Artist> search(String name, int page);

	/**
	 * 根据首字母，地区，性别类别筛选歌手
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @param page 目标页码
	 * @return 歌手列表
	 */
	List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page);
	
	/**
	 * 获取筛选后的歌手数目
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @return 歌手数目
	 */
	int getFilterCount(String initial, int region, int gender);

	/**
	 * 显示歌手详情
	 * @param id 歌手id
	 * @return 歌手
	 */
	Artist getInfo(int id);

	/**
	 * 添加歌手
	 * @param artist 歌手
	 * @return 新增的歌手ID
	 */
	int add(Artist artist);

	/**
	 * 删除歌手
	 * @param id 歌手ID
	 * @return 操作状态
	 */
	boolean remove(int id);

	/**
	 * 修改歌手信息
	 * @param artist 歌手
	 * @return 操作状态
	 */
	boolean modify(Artist artist);

	/**
	 * 设置歌手图片
	 * @param id 歌手id
	 * @param image 图片路径
	 * @return 操作状态
	 */
	boolean setImage(int id, String image);

	/**
	 * 查看歌手的歌曲列表
	 * @param id
	 * @return 歌曲列表
	 */
	List<Song> lookUpSongsByArtist(int id);

	/**
	 * 查看歌手的所有专辑
	 * @param id 歌手id
	 * @return 专辑列表
	 */
	List<Album> lookUpAlbumsByArtist(int id);

	/**
	 * 获取搜索结果的记录条数
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	int getSearchCount(String name);
	
}
