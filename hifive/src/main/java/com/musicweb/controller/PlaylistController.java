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
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
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
	public Integer create(HttpSession session, SimplePlaylistView simplePlaylistView) {
		String userId = (String)session.getAttribute(UserConstant.USER_ID);
		Playlist playlist = new Playlist();
		BeanUtils.copyProperties(simplePlaylistView, playlist);
		return playlistService.create(userId, playlist);
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public PlaylistView showInfo(int id, HttpSession session) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return null;
		Playlist playlist = playlistService.getInfo(id);
		PlaylistView playlistView = new PlaylistView();
		BeanUtils.copyProperties(playlist, playlistView);
		List<Song> songList = playlistService.getSongList(id);
		List<SimpleSongView> viewList = new ArrayList<SimpleSongView>();
		for(Song song: songList) {
			SimpleSongView view = new SimpleSongView();
			BeanUtils.copyProperties(song, view);
			viewList.add(view);
		}
		playlistView.setSongList(viewList);
		return playlistView;
	}

	@RequestMapping(value = "/modifyInfo", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyInfo(HttpSession session, SimplePlaylistView simplePlaylistView) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		Playlist playlist = new Playlist();
		BeanUtils.copyProperties(simplePlaylistView, playlist);
		return playlistService.modifyInfo(playlist);
	}
	
	@RequestMapping(value = "/setImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean setImage(HttpSession session, int playlistId, String image) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return playlistService.setImage(playlistId, image);
	}

	@RequestMapping(value = "/getSongs", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> getSongList(HttpSession session, int id) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return null;
		List<Song> songList = playlistService.getSongList(id);
		List<SimpleSongView> viewList = new ArrayList<SimpleSongView>();
		for(Song song: songList) {
			SimpleSongView view = new SimpleSongView();
			BeanUtils.copyProperties(song, view);
			viewList.add(view);
		}
		return viewList;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	@ResponseBody
	public Boolean remove(HttpSession session, int id) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return playlistService.remove(id);
	}

	@RequestMapping(value = "/addSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addASong(HttpSession session, int songId, int playlistId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return playlistService.addSong(playlistId, songId);
	}

	@RequestMapping(value = "/removeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeASong(HttpSession session, int playlistId, int songId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return  playlistService.removeSong(playlistId, songId);
	}

	@RequestMapping(value = "/addPlaylistToPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addPlaylistToPlaylist(HttpSession session, int fromId, int toId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return playlistService.addPlaylistToPlaylist(fromId, toId);
		
	}
	
	@RequestMapping(value = "/addAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addAlbumToPlaylist(HttpSession session, int albumId, int playlistId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return false;
		return playlistService.addAlbumToPlaylist(albumId, playlistId);
	}
	
	@RequestMapping(value = "/getPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimplePlaylistView> showPlaylists(HttpSession session) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		if(object == null) return null;
		String userId = (String)object;
		List<Playlist> playlistList = playlistService.getPlaylistList(userId);
		ArrayList<SimplePlaylistView> viewList = new ArrayList<SimplePlaylistView>();
		for(Playlist playlist: playlistList) {
			SimplePlaylistView view = new SimplePlaylistView();
			BeanUtils.copyProperties(playlist, view);
			viewList.add(view);
		}
		return viewList;
	}
	
}
