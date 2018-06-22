package com.musicweb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.constant.UserConstant;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.ArtistService;
import com.musicweb.service.CacheService;
import com.musicweb.view.ArtistView;
import com.musicweb.view.SimpleAlbumView;
import com.musicweb.view.SimpleArtistView;
import com.musicweb.view.SimpleSongView;

/**
 * ArtistController
 * @author likexin
 * @Date 2018/6/21
 * ArtistContoller负责接收前端的有关歌手模块的请求，并调用Service层的服务，业务完成后返回结果给前端
 */
@Controller
@RequestMapping("/artist")
public class ArtistController {
	@Resource
	private ArtistService artistService;
	@Resource
	private CacheService cacheService;
	
	/**
	 * 以名字为关键字搜索歌手
	 * @param name 歌手名字
	 * @param page 目标页码
	 * @return List<SimpleArtistView> 简单的歌手视图
	 */
	@RequestMapping(value = "/searchArtist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleArtistView> search(String name, int page) {//get
		List<Artist> artistList = artistService.search(name, page);
		List<SimpleArtistView> simpleArtistViewList = new ArrayList<SimpleArtistView>();
		//对每个Artist对象装配为ArtistView
		for(Artist artist:artistList) {
			SimpleArtistView view = new SimpleArtistView();
			BeanUtils.copyProperties(artist, view);
			simpleArtistViewList.add(view);
		}
		return simpleArtistViewList;
	}
	
	/**
	 * 根据首字母，地区，性别类别筛选歌手
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @param page 目标页码
	 * @return 简单歌手视图的列表
	 */
	@RequestMapping(value = "/filterArtist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleArtistView> filterArtistByCategory(String initial, int region, int gender, int page) {//get
		List<Artist> artistList = artistService.lookUpArtistsByCatagory(initial, region, gender, page);
		ArrayList<SimpleArtistView> simpleArtistViewList = new ArrayList<SimpleArtistView>();
		for(Artist artist:artistList) {
			SimpleArtistView view = new SimpleArtistView();
			BeanUtils.copyProperties(artist, view);
			simpleArtistViewList.add(view);
		}
		return simpleArtistViewList;
	}
	
	/**
	 * 获取筛选后的歌手数目
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @return 歌手数目
	 */
	@RequestMapping(value = "/filterArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer filterCount(String initial, int region, int gender) {
		return artistService.getFilterCount(initial, region, gender);
	}
	
	/**
	 * 显示歌手详情
	 * @param id 歌手ID
	 * @return 歌手视图
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public ArtistView showArtist(int id) {//get
		Artist artist = artistService.getInfo(id);
		ArtistView artistView = new ArtistView();
		if(artist == null) {
			return artistView;
		}
		BeanUtils.copyProperties(artist, artistView);
		
		//歌手的专辑列表
		ArrayList<SimpleAlbumView> simpleAlbumViewList = new ArrayList<SimpleAlbumView>();
		List<Album> albumList = artistService.lookUpAlbumsByArtist(id);
		for(Album album: albumList) {
			SimpleAlbumView view = new SimpleAlbumView();
			BeanUtils.copyProperties(album, view);
			simpleAlbumViewList.add(view);
		}
		//歌手的歌曲列表
		ArrayList<SimpleSongView> simpleSongViewList = new ArrayList<SimpleSongView>();
		List<Song> songList = artistService.lookUpSongsByArtist(id);
		for(Song song: songList) {
			SimpleSongView view =  new SimpleSongView();
			BeanUtils.copyProperties(song, view);
			simpleSongViewList.add(view);
		}
		
		//将歌手的专辑列表和歌曲列表设置入歌手视图
		artistView.setAlbumList(simpleAlbumViewList);
		artistView.setSongList(simpleSongViewList);
		return artistView;
		
	}
	
	/**
	 * 添加歌手
	 * @param artistView 歌手视图
	 * @param session 获取管理员ID
	 * @return 新增的歌手ID
	 */
	@RequestMapping(value = "/addArtist", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnArtist(ArtistView artistView, HttpSession session) {//post
		//从session中取出管理员id， 若为空则返回-1拒绝请求
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return -1;
		}
		Artist artist = new Artist();
		BeanUtils.copyProperties(artistView, artist);
		artist.setPlayCount(0);
		return artistService.add(artist);
	}
	
	/**
	 * 删除歌手
	 * @param id 歌手ID
	 * @param session 获取管理员id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/removeArtist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnArtist(int id, HttpSession session) {//get
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return false;
		}
		return artistService.remove(id);
	}
	
	/**
	 * 修改歌手信息
	 * @param artistView 歌手视图
	 * @param session 获取管理员id
	 * @return 操作状态
	 */
	@RequestMapping(value = "/modifyArtist", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnArtist(ArtistView artistView, HttpSession session) {//post
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return false;
		}
		Artist artist = new Artist();
		BeanUtils.copyProperties(artistView, artist);
		//补充该歌手的playcount属性
		Artist artistCache = cacheService.getAndCacheSingerBySingerID(artistView.getId());
		artist.setPlayCount(artistCache.getPlayCount());
		return artistService.modify(artist);
	}
	
	/**
	 * 获取搜索结果的记录条数
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	@RequestMapping(value = "/searchArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return artistService.getSearchCount(name);
	}

}
