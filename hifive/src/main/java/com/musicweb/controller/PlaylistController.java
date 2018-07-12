package com.musicweb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 * PlaylistContoller
 * @author likexin
 * PlaylistContoller负责接收前端的有关歌单模块的请求，并调用Service层的服务，业务完成后返回结果给前端
 * 有关歌单的所有操作需要在用户登录状态下进行, 所有方法都要通过session获取用户ID以判断用户是否是出于登录状态
 */
@Controller
@RequestMapping("/playlist")
public class PlaylistController {
	
	@Resource
	private PlaylistService playlistService;
	
	@Resource
	private UserService userService;
	
	private String testUserId = "public@qq.com";
	private String testAdminId = "public2@qq.com";

	/**
	 * 创建歌单
	 * @param session 获取用户ID
	 * @param simplePlaylistView 前端传进的新建歌单的信息
	 * @return 新建歌单的ID
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public Integer create(HttpSession session, @RequestBody SimplePlaylistView simplePlaylistView) {
		String userId = (String)session.getAttribute(UserConstant.USER_ID);
		
		//test
		//userId = testUserId;
		
		
		if(userId == null) return -1;
		Playlist playlist = new Playlist();
		BeanUtils.copyProperties(simplePlaylistView, playlist);
		return playlistService.create(userId, playlist);
	}
	
	/**
	 * 显示歌单信息
	 * @param id 歌单ID
	 * @param session 获取用户ID
	 * @return 歌单信息视图
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public PlaylistView showInfo(int id, HttpSession session) {
		//用户离线返回空
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return null;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, id))
			return new PlaylistView();

		Playlist playlist = playlistService.getInfo(id);
		PlaylistView playlistView = new PlaylistView();
		BeanUtils.copyProperties(playlist, playlistView);
		//获取歌单的歌单列表
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

	/**
	 * 修改歌单信息
	 * @param session 获取用户ID
	 * @param simplePlaylistView 存有修改的歌单信息的歌单视图
	 * @return 修改歌单信息状态
	 */
	@RequestMapping(value = "/modifyInfo", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyInfo(HttpSession session, @RequestBody SimplePlaylistView simplePlaylistView) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, simplePlaylistView.getId()))
			return false;
		
		Playlist playlist = new Playlist();
		BeanUtils.copyProperties(simplePlaylistView, playlist);
		return playlistService.modifyInfo(playlist);
	}
	
	/**
	 * 设置歌单图片
	 * @param session 获取用户ID
	 * @param playlistId 歌单ID
	 * @param image 上传的图片在服务器的路径
	 * @return 设置歌单图片状态
	 */
	@RequestMapping(value = "/setImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean setImage(HttpSession session, int playlistId, String image) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, playlistId))
			return false;
		
		return playlistService.setImage(playlistId, image);
	}

	/**
	 * 获取歌单的歌曲列表
	 * @param session 获取用户ID
	 * @param id 歌单ID
	 * @return 歌单的歌曲列表视图
	 */
	@RequestMapping(value = "/getSongs", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> getSongList(HttpSession session, int id) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return null;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, id))
			return new ArrayList<>();
		
		List<Song> songList = playlistService.getSongList(id);
		//将歌曲列表装配为歌曲视图列表
		List<SimpleSongView> viewList = new ArrayList<SimpleSongView>();
		for(Song song: songList) {
			SimpleSongView view = new SimpleSongView();
			BeanUtils.copyProperties(song, view);
			viewList.add(view);
		}
		return viewList;
	}

	/**
	 * 删除歌单
	 * @param session 获取用户ID
	 * @param id 歌单ID
	 * @return 删除歌单的状态
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	@ResponseBody
	public Boolean remove(HttpSession session, int id) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, id))
			return false;
		return playlistService.remove(id);
	}

	/**
	 * 为歌单新增歌曲
	 * @param session 获取用户ID
	 * @param songId 新增的歌曲ID
	 * @param playlistId 歌单ID
	 * @return 新增歌曲状态
	 */
	@RequestMapping(value = "/addSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addASong(HttpSession session, int songId, int playlistId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, playlistId))
			return false;
		return playlistService.addSong(playlistId, songId);
	}

	/**
	 * 从歌单中删除歌曲
	 * @param session 获取用户ID
	 * @param playlistId 歌单ID
	 * @param songId 移除的歌单ID
	 * @return 移除歌曲状态
	 */
	@RequestMapping(value = "/removeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeASong(HttpSession session, int playlistId, int songId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, playlistId))
			return false;
		
		return  playlistService.removeSong(playlistId, songId);
	}

	/**
	 * 向歌单从添加另一歌单所有歌曲
	 * @param session 获取用户ID
	 * @param fromId 被复制的歌单ID
	 * @param toId 新增歌曲的歌单ID
	 * @return 向歌单从添加另一歌单所有歌曲状态
	 */
	@RequestMapping(value = "/addPlaylistToPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addPlaylistToPlaylist(HttpSession session, int fromId, int toId) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, fromId))
			return false;
		if(!playlistService.checkPossession(userId, toId))
			return false;
		
		return playlistService.addPlaylistToPlaylist(fromId, toId);
		
	}
	
	/**
	 * 向歌单中添加专辑
	 * @param session 获取用户ID
	 * @param albumId 专辑ID
	 * @param playlistId 歌单ID
	 * @return 向歌单添加专辑状态
	 */
	@RequestMapping(value = "/addAlbum", method = RequestMethod.GET)
	@ResponseBody
	public Boolean addAlbumToPlaylist(HttpSession session, int albumId, int playlistId) {
		Object object = session.getAttribute(UserConstant.USER_ID);	
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return false;
		
		//验证用户是否拥有此歌单
		if(!playlistService.checkPossession(userId, playlistId))
			return false;
		return playlistService.addAlbumToPlaylist(albumId, playlistId);
	}
	
	/**
	 * 显示用户的歌单列表
	 * @param session 获取用户ID
	 * @return 歌单列表视图
	 */
	@RequestMapping(value = "/getPlaylist", method = RequestMethod.GET)
	@ResponseBody
	public List<SimplePlaylistView> showPlaylists(HttpSession session) {
		Object object = session.getAttribute(UserConstant.USER_ID);
		String userId = (String)object;
		
		//test
		//userId = testUserId;
		
		if(userId == null) return null;

		List<Playlist> playlistList = playlistService.getPlaylistList(userId);
		System.out.println(playlistList.size());
		if(playlistList == null)
			playlistList = new ArrayList<>();
		ArrayList<SimplePlaylistView> viewList = new ArrayList<SimplePlaylistView>();
		for(Playlist playlist: playlistList) {
			SimplePlaylistView view = new SimplePlaylistView();
			BeanUtils.copyProperties(playlist, view);
			//SimplePlaylistView中的count，指明歌单中的歌曲数量，的属性需要自行获取添加
			int count = playlistService.getSongList(playlist.getId()).size();
			view.setCount(count);
			viewList.add(view);
		}
		return viewList;
	}
	
}
