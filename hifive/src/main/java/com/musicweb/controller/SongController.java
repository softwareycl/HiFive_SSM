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
import com.musicweb.domain.Song;
import com.musicweb.service.SongService;
import com.musicweb.util.DurationUtil;
import com.musicweb.view.SimpleSongView;
import com.musicweb.view.SongView;

/**
 * 歌曲模块控制器类
 * 
 * @author brian
 * 
 */
@Controller
@RequestMapping("/song")
public class SongController {
	/**
	 * 歌曲模块业务逻辑类
	 */
	@Resource
	private SongService songService;
	
	private String testUserId = "public@qq.com";
	private String testAdminId = "public2@qq.com";
	
	/**
	 * 用户或管理员搜索歌曲
	 * 
	 * @param name 输入的歌曲名称，或歌曲名称的一部分
	 * @param page 目标页号
	 * @return 歌曲列表
	 */
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
	
	/**
	 * 用户或管理员得到歌曲搜索结果的数目
	 * 
	 * @param name 输入的歌曲名称，或歌曲名称的一部分
	 * @return 歌曲搜索结果的数目
	 */
	@RequestMapping(value = "/searchSongCount", method = RequestMethod.GET)
	@ResponseBody
	public Integer searchCount(String name) {//get
		int count = songService.getSearchCount(name);
		return count;
	}
	
	/**
	 * 用户查看歌曲排行榜
	 * 
	 * @param type 排行榜类型
	 * @param isAll 排行榜的位置
	 * @return 歌曲列表
	 */
	@RequestMapping(value = "/rank", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> showRank(int type, boolean isAll) {//get
		List<Song> songs = songService.lookUpRank(type, isAll);
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
	
	/**
	 * 用户或管理员查看歌曲详情
	 * 
	 * @param id 歌曲id
	 * @return 歌曲详情
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.GET)
	@ResponseBody
	public SongView showInfo(HttpSession session, int id) {//get
		//test
		session.setAttribute(UserConstant.USER_ID, testUserId);
		
		Song song = songService.getInfo(id);
		SongView songView = new SongView();
		if (session.getAttribute(UserConstant.USER_ID) == null) {
			songView.setOnline(false);
		}
		if(song != null) {
			BeanUtils.copyProperties(song, songView);
			songView.setDuration(songService.getDuration(song.getId()));
		}
		return songView;
	}
	
	/**
	 * 用户播放歌曲，后台增加该歌曲播放量
	 * 
	 * @param id 歌曲id
	 */
	@RequestMapping(value = "/play", method = RequestMethod.GET)
	@ResponseBody
	public void play(int id) {//get
		songService.play(id);
	}
	
	/**
	 * 管理员添加歌曲
	 * 
	 * @param songView 将被添加的歌曲
	 * @return 新添加的歌曲id
	 */
	@RequestMapping(value = "/addSong", method = RequestMethod.POST)
	@ResponseBody
	public Integer addASong(@RequestBody SongView songView) {//返回值问题
		Song song = new Song();
		BeanUtils.copyProperties(songView, song);
		int songId = songService.add(song);
		return songId;
	}
	
	/**
	 * 管理员删除歌曲
	 * 
	 * @param songId 将被删除的歌曲id
	 * @return 布尔值，表示成功或失败
	 */
	@RequestMapping(value = "/removeSong", method = RequestMethod.GET)
	@ResponseBody
	public Boolean removeASong(int songId) {//get
		boolean b = songService.remove(songId);
		return b;
	}
	
	/**
	 * 管理员编辑歌曲
	 * 
	 * @param songView 所修改的歌曲内容
	 * @return 布尔值，表示成功或失败
	 */
	@RequestMapping(value = "/modifySong", method = RequestMethod.POST)
	@ResponseBody
	public Boolean modifyASong(@RequestBody SongView songView) {//post
		Song song = new Song();
		BeanUtils.copyProperties(songView, song);
		boolean b = songService.modify(song);
		return b;
	}
	
	/**
	 * 用户查看新歌首发
	 * 
	 * @param region 地区
	 * @return 歌曲列表
	 */
	@RequestMapping(value = "/getNewSongs", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleSongView> showNewSongs(int region) {//service未搞定，先不写
		List<Song> songs = songService.lookUpNewSongs(region);
		List<SimpleSongView> songViews = new ArrayList<>();
		if(songs != null) {
			for(Song song: songs) {
				SimpleSongView songView = new SimpleSongView();
				BeanUtils.copyProperties(song, songView);
				songViews.add(songView);
			}
			return songViews;
		}
		return null;
	}

}
