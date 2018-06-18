package com.musicweb.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.service.PlaylistService;
import com.musicweb.service.UserService;
import com.musicweb.view.PlaylistView;
import com.musicweb.view.SimplePlaylistView;
import com.musicweb.view.SimpleSongView;

@Controller
@RequestMapping("/playlist")
public class PlaylistController {
	
	@Resource
	private PlaylistService playlistService;
	@Resource
	private UserService userService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public Integer create(HttpSession session, SimplePlaylistView playlist) {
		return 0;
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public PlaylistView showInfo(int id, HttpSession session) {
		return null;
	}

	@RequestMapping(value = "/modifyInfo", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyInfo(HttpSession session, SimplePlaylistView playlist) {
		return false;
	}

	@RequestMapping(value = "/getSongs", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> getSongList(HttpSession session, int id) {
		return null;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	@ResponseBody
	public Boolean remove(HttpSession session, int id) {
		return false;
	}

	@RequestMapping(value = "/addSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addASong(HttpSession session, int songId, int playlistId) {
		return false;
	}

	@RequestMapping(value = "/removeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeASong(HttpSession session, int playlistId, int songId) {
		return false;
	}

	@RequestMapping(value = "/addPlaylistToPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addPlaylistToPlaylist(HttpSession session, int fromId, int toId) {
		return false;
	}
	
	@RequestMapping(value = "/addAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addAlbumToPlaylist(HttpSession session, int albumId, int playlistId) {
		return false;
	}
	
	@RequestMapping(value = "/getPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimplePlaylistView> showPlaylists(HttpSession session) {
		return null;
	}
	
}
