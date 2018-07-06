package com.musicweb.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.musicweb.constant.UserConstant;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.AlbumService;
import com.musicweb.service.ArtistService;
import com.musicweb.service.PlaylistService;
import com.musicweb.service.SongService;
import com.musicweb.service.UserService;

/**
 * 负责文件上传，包括用户头像，歌曲图片，专辑封面，歌手图片，歌曲音频文件，歌词文件
 * 
 * @author brian
 */
@Controller
@RequestMapping("/upload")
public class UploadController {
	
	/**
	 * 用来拼接绝对路径
	 */
	private String classPath = this.getClass().getClassLoader().getResource("").getPath();
	private String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
	
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
	
	/**
	 * 上传用户头像
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadUserImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadUserImage(HttpSession session, MultipartHttpServletRequest request) {
		String id = (String) session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		id = "public@qq.com";
		
		if(id == null) return false;
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		User user = userService.getInfo(id);
        		String prefix = "/image/user/" + user.getId() + "/";//获取用户头像路径
        		String absolute = WebInfoPath + prefix;
        		if(!new File(absolute).exists())
        			new File(absolute).mkdirs();
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		userService.setImage(id, prefix + fileName);
        	}
        }
		return true;
	}

	/**
	 * 上传歌单封面
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadPlaylistImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadPlaylistImage(HttpSession session, MultipartHttpServletRequest request) {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public@qq.com";
		
		if(userId == null) return false;
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Playlist playlist = playlistService.getInfo(id);
        		String prefix = "/image/user/" + userId + "/" + playlist.getId() + "/";//获取歌单图片路径
        		String absolute = WebInfoPath + prefix;
        		String filePath = absolute + fileName;
        		
        		//新建文件夹
        		String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));
        		new File(folderPath).mkdirs();
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		playlistService.setImage(id, prefix + fileName);
        	}
        }
		return false;
	}

	/**
	 * 上传专辑封面
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 * @throws Exception 
	 */
	@RequestMapping(value = "/uploadAlbumImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadAlbumImage(HttpSession session, MultipartHttpServletRequest request) throws Exception {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public2@qq.com";

		if(userId == null) return false;
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		System.out.println(id);
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Album album = albumService.getInfo(id);
        		String prefix = "/image/album/" + album.getArtistName() + "/";//获取专辑图片路径
        		String absolute = WebInfoPath + prefix;
        		if (!new File(absolute).exists()) {
					new File(absolute).mkdirs();
				}
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		albumService.setImage(id, prefix + fileName);
        	}
        }
		return true;
	}

	/**
	 * 上传歌手图片
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadArtistImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadArtistImage(HttpSession session, MultipartHttpServletRequest request) {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public2@qq.com";
		
		if(userId == null) return false;
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		System.out.println(id);
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Artist artist = artistService.getInfo(id);
        		String prefix = "/image/singer/";//获取歌手图片路径
        		String absolute = WebInfoPath + prefix;
        		if(!new File(absolute).exists())
        			new File(absolute).mkdirs();
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		artistService.setImage(id, prefix + fileName);
        	}
        }
		
		return true;
	}

	/**
	 * 上传歌曲图片
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadSongImage", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadSongImage(HttpSession session, MultipartHttpServletRequest request) {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public2@qq.com";
		
		if(userId == null) return false;
		
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		System.out.println(id);
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Song song = songService.getInfo(1);
        		String prefix = "/image/song/" + song.getArtistName() + "/" + song.getAlbumName() + "/";//获取歌曲图片路径
        		String absolute = WebInfoPath + prefix;
        		if (!new File(absolute).exists()) {
					new File(absolute).mkdirs();
				}
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		songService.setImage(id, prefix + fileName);
        	}
        }
		return true;
	}

	/**
	 * 上传歌词文件
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadLyrics", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadlyrics(HttpSession session, MultipartHttpServletRequest request) {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public2@qq.com";
		
		if(userId == null) return false;
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		System.out.println(id);
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Song song = songService.getInfo(1);
        		String prefix = "/lyrics/" + song.getArtistName() + "/" + song.getAlbumName() + "/";//获取歌词路径
        		String absolute = WebInfoPath + prefix;
        		if (!new File(absolute).exists()) {
					new File(absolute).mkdirs();
				}
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		songService.setLyricsPath(id, prefix + fileName);
        	}
        }
		return true;
	}

	/**
	 * 上传歌曲音频文件
	 * 
	 * @param session 浏览器与后台的session对象
	 * @param request 包含文件流的http request
	 * @return true表示成功，false表示失败
	 */
	@RequestMapping(value = "/uploadSongFile", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadSong(HttpSession session, MultipartHttpServletRequest request) {
		String userId = (String)session.getAttribute(UserConstant.ADMIN_ID);
		
		//test
		userId = "public2@qq.com";
		
		if(userId == null) return false;
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		System.out.println(id);
		
		Iterator<?> iter = request.getFileNames();
        if (iter.hasNext()) {
        	String fileName = iter.next().toString();
        	MultipartFile file = request.getFile(fileName);//从request中获取文件
        	if (file != null) {
        		fileName = file.getOriginalFilename();
        		Song song = songService.getInfo(1);
        		String prefix = "/music/" + song.getArtistName() + "/" + song.getAlbumName() + "/";//获取歌曲音频文件路径
        		String absolute = WebInfoPath + prefix;
        		if (!new File(absolute).exists()) {
					new File(absolute).mkdirs();
				}
        		String filePath = absolute + fileName;
        		try {
        			//将文件存储在硬盘上
					file.transferTo(new File(filePath));
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return false;
				}
        		songService.setFilePath(id, prefix + fileName);
        	}
        }
		return true;
	}
	
}
