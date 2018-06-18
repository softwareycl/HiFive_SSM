package com.musicweb.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musicweb.service.AlbumService;
import com.musicweb.service.ArtistService;
import com.musicweb.service.PlaylistService;
import com.musicweb.service.SongService;
import com.musicweb.service.UserService;

@Controller
@RequestMapping("/upload")
public class UploadController {
	
	@Resource
	private UserService userService;
	@Resource
	private AlbumService albumService;
	@Resource
	private ArtistService artistService;
	@Resource
	private PlaylistService playlistService;
	@Resource
	private SongService songService;
	
	@RequestMapping(value = "/uploadUserImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadUserImage(HttpServletRequest request, HttpSession session) {
		return false;
	}

	@RequestMapping(value = "/uploadAlbumImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadAlbumImage(HttpServletRequest request, int id) {
		return false;
	}

	@RequestMapping(value = "/uploadArtistImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadArtistImage(HttpServletRequest request, int id) {
		return false;
	}

	@RequestMapping(value = "/uploadLyrics", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadlyrics(HttpServletRequest request, int id) {
		return false;
	}

	@RequestMapping(value = "/uploadSongFile", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadSong(HttpServletRequest request, int id) {
		return false;
	}
	
	@RequestMapping(value = "/uploadSongImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadSongImage(HttpServletRequest request, int id) {
		return false;
	}

	@RequestMapping(value = "/uploadPlaylistImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadPlaylistImage(HttpServletRequest request, int id) {
		return false;
	}
}
