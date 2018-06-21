package com.musicweb.controller;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
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

@Controller
@RequestMapping("/album")
public class AlbumController {
	@Resource
	private AlbumService albumService;

	@RequestMapping(value = "/searchAlbumCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return albumService.getSearchCount(name);
	}
	
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
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public AlbumView showAlbum(int id, HttpSession session) {//get
		Album album = albumService.getInfo(id);
		AlbumView albumView = new AlbumView();
		//判断用户是否在线
		boolean isOnline = (session.getAttribute(UserConstant.USER_ID) == null && session.getAttribute(UserConstant.ADMIN_ID) == null) ? false : true;
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

	@RequestMapping(value = "/modifyAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnAlbum(AlbumView album, HttpSession session) {//post
		boolean isOnline = session.getAttribute(UserConstant.ADMIN_ID) == null ? false : true;
		if(isOnline == false)
			return false;
		Album al = new Album();
		BeanUtils.copyProperties(album, al);
		return albumService.modify(al);
	}
	
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
	
	@RequestMapping(value = "/filterAlbumCount", method = RequestMethod.GET)
	@ResponseBody
	public int filterCount(int region, int style) {//get
		return albumService.getFilterCount(region, style);
	}
	
	@RequestMapping(value = "/addAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnAlbum(AlbumView album, HttpSession session) {//post
		if(session.getAttribute(UserConstant.ADMIN_ID) == null)
			return -1;
		Album al = new Album();
		BeanUtils.copyProperties(album, al);
		int id = albumService.add(al);
		return id;
	}
	
	@RequestMapping(value = "/removeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnAlbum(int id, HttpSession session) {//get
		if(session.getAttribute(UserConstant.ADMIN_ID) == null)
			return false;
		
		return albumService.remove(id);
	}
	
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
