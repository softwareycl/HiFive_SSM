package com.musicweb.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.service.ArtistService;
import com.musicweb.view.ArtistView;
import com.musicweb.view.SimpleArtistView;

@Controller
@RequestMapping("/artist")
public class ArtistController {
	@Resource
	private ArtistService artistService;
	
	@RequestMapping(value = "/searchArtist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleArtistView> search(String name, int page) {//get
		return null;
	}
	
	@RequestMapping(value = "/filterArtist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleArtistView> filterArtistByCategory(String initial, int region, int gender, int page) {//get
		return null;
	}
	
	@RequestMapping(value = "/filterArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer filterCount(String initial, int region, int gender) {//get
		return 0;
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public ArtistView showArtist(int id) {//get
		return null;
	}
	
	@RequestMapping(value = "/addArtist", method = RequestMethod.POST)
	@ResponseBody
	public Integer addAnArtist(ArtistView artist, HttpSession session) {//post
		return 1;
	}
	
	@RequestMapping(value = "/removeArtist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeAnArtist(int id) {//get
		return false;
	}
	
	@RequestMapping(value = "/modifyArtist", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyAnArtist(ArtistView artist) {//post
		return false;
	}
	
	@RequestMapping(value = "/searchArtistCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return 0;
	}

}
