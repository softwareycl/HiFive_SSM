package com.musicweb.controller;

import java.util.*;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
		return 0;
	}
	
	@RequestMapping(value = "/searchAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> search(String name, int page) {//get
		return null;
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public AlbumView showAlbum(int id) {//get
		return null;
	}

	@RequestMapping(value = "/modifyAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnAlbum(SimpleAlbumView album) {//post
		return false;
	}
	
	@RequestMapping(value = "/filterAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> filterAlbumByCategory(int region, int style, int page) {//get
		return null;
	}
	
	@RequestMapping(value = "/filterAlbumCount", method = RequestMethod.GET)
	@ResponseBody
	public int filterCount(int region, int style) {//get
		return 0;
	}
	
	@RequestMapping(value = "/addAlbum", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnAlbum(AlbumView album) {//post
		return 0;
	}
	
	@RequestMapping(value = "/removeAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnAlbum(int id) {//get
		return false;
	}
	
	@RequestMapping(value = "/getNewAlbums", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleAlbumView> showNewAlbums(int region) {//get
		return null;
	}
	
	@RequestMapping(value = "/getSongsFromAlbum", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> getSongList(int id) {
		return null;
	}

}
