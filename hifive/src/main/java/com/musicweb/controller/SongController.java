package com.musicweb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.domain.Song;
import com.musicweb.service.SongService;
import com.musicweb.view.SimpleSongView;
import com.musicweb.view.SongView;

@Controller
@RequestMapping("/song")
public class SongController {
	@Resource
	private SongService songService;
	
	@RequestMapping(value = "/searchSong", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> search(String name, int page) {//get
		List<Song> songs = songService.search(name, page);
		List<SimpleSongView> songViews = new ArrayList<>();
		if(songs == null)
			return songViews;
		for(Song song: songs) {
			SimpleSongView songView = new SimpleSongView();
			BeanUtils.copyProperties(song, songView);
			songView.setDuration(songService.getDuration(song.getId()));
			songViews.add(songView);
		}
		return songViews;
	}
	
	@RequestMapping(value = "/rank", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> showRank(int type, boolean isAll) {//get
		return null;
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public SongView showInfo(int id) {//get
		return null;
	}
	
	@RequestMapping(value = "/play", method = RequestMethod.GET)
	@ResponseBody
	public void play(int id) {//get
		
	}
	
	@RequestMapping(value = "/addSong", method = RequestMethod.POST)
	@ResponseBody
	public Integer addASong(SongView song) {//post
		return 0;
	}
	
	@RequestMapping(value = "/removeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeASong(int songId) {//get
		return false;
	}
	
	@RequestMapping(value = "/modifySong", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyASong(SongView song) {//post
		return false;
	}
	
	@RequestMapping(value = "/searchSongCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		return 0;
	}
	
	@RequestMapping(value = "/getNewSongs", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> showNewSongs(int region) {//get
		return null;
	}

}
