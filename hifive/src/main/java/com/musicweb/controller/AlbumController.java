package com.musicweb.controller;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.constant.UserConstant;
import com.musicweb.domain.Album;
import com.musicweb.domain.Song;
import com.musicweb.service.AlbumService;
import com.musicweb.view.AlbumView;
import com.musicweb.view.SimpleAlbumView;
import com.musicweb.view.SimpleSongView;

/**
 * AlbumController
 * @author zhanghuakui
 * @Date 2018/6/23
 * AlbumController负责接收前端的有关歌手模块的请求，并调用Service层的服务，业务完成后返回结果给前端
 */
@Controller
@RequestMapping("/album")
public class AlbumController {
	@Resource
	private AlbumService albumService;
	
	private String testUserId = "public@qq.com";
	private String testAdminId = "public2@qq.com";

	/**
	 * 获取搜索结果的记录条数
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	@RequestMapping(value = "/searchAlbumCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return albumService.getSearchCount(name);
	}
	
	/**
	 * 以名字为关键字搜索专辑
	 * @param name 专辑名字
	 * @param page 目标页码
	 * @return List<SimpleAlbumView> 专辑视图
	 */
	@RequestMapping(value = "/searchAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> search(String name, int page) {//get
		List<Album> albumList = albumService.search(name, page);
		List<SimpleAlbumView> simpleAlbumList = new ArrayList<>();
		for(Album album: albumList) {
			SimpleAlbumView simpleAlbumView = new SimpleAlbumView();
			BeanUtils.copyProperties(album, simpleAlbumView);
			simpleAlbumList.add(simpleAlbumView);
		}
		
		return simpleAlbumList;
	}
	
	/**
	 * 显示专辑详情
	 * @param id 专辑ID
	 * @return 专辑视图，包括专辑包括的歌曲列表
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public AlbumView showAlbum(int id, HttpSession session) {//get
		Album album = albumService.getInfo(id);
		AlbumView albumView = new AlbumView();
		//判断用户是否在线
		boolean isOnline = (session.getAttribute(UserConstant.USER_ID) == null && session.getAttribute(UserConstant.ADMIN_ID) == null) ? false : true;
		
		//test
		isOnline = true;
		
		albumView.setOnline(isOnline);
		if (album == null) return albumView;
		BeanUtils.copyProperties(album, albumView);

		//专辑歌曲
		List<Song> songList = albumService.getSongList(id);
		if(songList != null) {
			ArrayList<SimpleSongView> simpleSongList = new ArrayList<>();
			for(Song song : songList) {
				SimpleSongView simpleSongView = new SimpleSongView();
				BeanUtils.copyProperties(song, simpleSongView);
				simpleSongList.add(simpleSongView);
			}
			albumView.setSongList(simpleSongList);
		}
		return albumView;
	}

	/**
	 * 修改专辑信息
	 * @param albumView 专辑视图
	 * @param session 获取管理员id，检测用户合法性
	 * @return 操作状态
	 */
	@RequestMapping(value = "/modifyAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnAlbum(@RequestBody AlbumView album, HttpSession session) {//post
		boolean isOnline = session.getAttribute(UserConstant.ADMIN_ID) == null ? false : true;
		
		//test
		isOnline = true;
		
		if(isOnline == false)
			return false;
		Album al = new Album();
		BeanUtils.copyProperties(album, al);
		return albumService.modify(al);
	}
	
	/**
	 * 获取筛选后的专辑数目
	 * @param region 地区
	 * @param style 风格
	 * @return 专辑数目
	 */
	@RequestMapping(value = "/filterAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> filterAlbumByCategory(int region, int style, int page) {//get
		List<Album> albumList = albumService.lookUpAlbumsByCatagory(region, style, page);
		List<SimpleAlbumView> simpleAlbumList = new ArrayList<>();
		if(albumList == null) return simpleAlbumList;
		for(Album album: albumList) {
			SimpleAlbumView simpleAlbumView = new SimpleAlbumView();
			BeanUtils.copyProperties(album, simpleAlbumView);
			simpleAlbumList.add(simpleAlbumView);
		}
		return simpleAlbumList;
	}
	
	/**
	 * 根据地区，风格类别筛选歌手
	 * @param region 地区
	 * @param style 风格
	 * @param page 目标页码
	 * @return 专辑视图的列表
	 */
	@RequestMapping(value = "/filterAlbumCount", method = RequestMethod.GET)
	@ResponseBody
	public int filterCount(int region, int style) {//get
		return albumService.getFilterCount(region, style);
	}
	
	/**
	 * 添加专辑
	 * @param albumView 专辑视图
	 * @param session 用于获取管理员ID，检测用户合法性
	 * @return 新增的专辑ID
	 */
	@RequestMapping(value = "/addAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnAlbum(@RequestBody AlbumView album, HttpSession session) {//post
		
		//test，部署时将下列语句取消注释
//		if(session.getAttribute(UserConstant.ADMIN_ID) == null)
//			return -1;
		
		Album al = new Album();
		BeanUtils.copyProperties(album, al);
		int id = albumService.add(al);
		return id;
	}
	
	/**
	 * 删除专辑
	 * @param id 专辑ID
	 * @param session 获取管理员id，检验用户合法性
	 * @return 操作状态
	 */
	@RequestMapping(value = "/removeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnAlbum(int id, HttpSession session) {//get
		//test，部署时将下列语句取消注释
		//if(session.getAttribute(UserConstant.ADMIN_ID) == null)
		//	return false;
		
		return albumService.remove(id);
	}
	
	/**
	 * 获取最新专辑
	 * @param region 地区
	 * @return 专辑列表
	 */
	@RequestMapping(value = "/getNewAlbums", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> showNewAlbums(int region) {//get
		List<Album> albumList = albumService.lookUpNewAlbums(region);
		List<SimpleAlbumView> simpleAlbumList = new ArrayList<>();
		if(albumList != null)
			for(Album album: albumList) {
				SimpleAlbumView simpleAlbumView = new SimpleAlbumView();
				BeanUtils.copyProperties(album, simpleAlbumView);
				simpleAlbumList.add(simpleAlbumView);
			}
		return simpleAlbumList;
	}
	
	/**
	 * 获取专辑所属歌曲列表
	 * @param id 专辑id
	 * @return 歌曲列表
	 */
	@RequestMapping(value = "/getSongsFromAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> getSongList(int id) {
		List<Song> songList = albumService.getSongList(id);
		ArrayList<SimpleSongView> simpleSongViews = new ArrayList<>();
		if(songList != null) {
			for(Song song: songList) {
				SimpleSongView songView = new SimpleSongView();
				BeanUtils.copyProperties(song, songView);
				simpleSongViews.add(songView);
			}
		}
		return simpleSongViews;
	}

}
