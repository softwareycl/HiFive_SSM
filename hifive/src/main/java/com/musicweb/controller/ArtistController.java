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

@Controller
@RequestMapping("/artist")
public class ArtistController {
	@Resource
	private ArtistService artistService;
	@Resource
	private CacheService cacheService;
	
	@RequestMapping(value = "/searchArtist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleArtistView> search(String name, int page) {//get
		List<Artist> artistList = artistService.search(name, page);
		List<SimpleArtistView> simpleArtistViewList = new ArrayList<SimpleArtistView>();
		for(Artist artist:artistList) {
			SimpleArtistView view = new SimpleArtistView();
			BeanUtils.copyProperties(artist, view);
			simpleArtistViewList.add(view);
		}
		return simpleArtistViewList;
	}
	
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
	
	@RequestMapping(value = "/filterArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer filterCount(String initial, int region, int gender) {
		return artistService.getFilterCount(initial, region, gender);
	}
	
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
		
		artistView.setAlbumList(simpleAlbumViewList);
		artistView.setSongList(simpleSongViewList);
		return artistView;
		
	}
	
	@RequestMapping(value = "/addArtist", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnArtist(ArtistView artistView, HttpSession session) {//post
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return -1;
		}
		Artist artist = new Artist();
		BeanUtils.copyProperties(artistView, artist);
		artist.setPlayCount(0);
		return artistService.add(artist);
	}
	
	@RequestMapping(value = "/removeArtist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnArtist(int id,HttpSession session) {//get
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return false;
		}
		return artistService.remove(id);
	}
	
	@RequestMapping(value = "/modifyArtist", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnArtist(ArtistView artistView, HttpSession session) {//post
		Object object = session.getAttribute(UserConstant.ADMIN_ID);
		if(object == null) {
			return false;
		}
		Artist artist = new Artist();
		BeanUtils.copyProperties(artistView, artist);
		Artist artistCache = cacheService.getAndCacheSingerBySingerID(artistView.getId());
		artist.setPlayCount(artistCache.getPlayCount());
		return artistService.modify(artist);
	}
	
	@RequestMapping(value = "/searchArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return artistService.getSearchCount(name);
	}

}
