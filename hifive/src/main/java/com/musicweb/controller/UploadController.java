package com.musicweb.controller;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.musicweb.constant.UserConstant;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.service.AlbumService;
import com.musicweb.service.ArtistService;
import com.musicweb.service.PlaylistService;
import com.musicweb.service.SongService;
import com.musicweb.service.UserService;
import com.musicweb.view.PicAttr;

/**
 * 负责文件上传，包括用户头像，歌曲图片，专辑封面，歌手图片，歌曲音频文件，歌词文件
 * 
 * @author brian
 */
@Controller
@RequestMapping("/upload")
public class UploadController {
	
	/**
	 * 暂存文件
	 */
	private File avatar_file;
	
	/**
	 * 保存头像属性，用于裁剪头像
	 */
	private PicAttr headPicAttr;
	
	/**
	 * 
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
	
	@RequestMapping(value = "/uploadUserImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadUserImage(HttpSession session, CommonsMultipartFile file, String avatar_data) {
		String id = (String) session.getAttribute(UserConstant.USER_ID);
		String fileName = file.getOriginalFilename();
		String prefix = WebInfoPath + "/image/user/" + userService.getInfo(id).getName() + "/";//获取用户头像路径
		String filePath = prefix + fileName;
		avatar_file = new File(filePath);
		try {
			file.transferTo(avatar_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		//裁剪头像
		headPicAttr = JSON.parseObject(avatar_data, PicAttr.class);
		try {
			cut(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		userService.setImage(id, filePath);
		return true;
	}

	@RequestMapping(value = "/uploadAlbumImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadAlbumImage(CommonsMultipartFile file, int id) {
		String fileName = file.getOriginalFilename();
		String prefix = WebInfoPath + "/image/album/" + albumService.getInfo(id).getArtistName() + "/";//获取专辑图片路径
		String filePath = prefix + fileName;
		avatar_file = new File(filePath);
		try {
			file.transferTo(avatar_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		albumService.setImage(id, filePath);
		return true;
	}

	@RequestMapping(value = "/uploadArtistImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadArtistImage(CommonsMultipartFile file, int id) {
		String fileName = file.getOriginalFilename();
		String prefix = WebInfoPath + "/singer/";//获取歌手头像路径
		String filePath = prefix + fileName;
		avatar_file = new File(filePath);
		try {
			file.transferTo(avatar_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		artistService.setImage(id, filePath);
		return true;
	}

	/**
	 * 管理员上传歌词文件
	 * 
	 * @param request 包含歌词文件的http request
	 * @param id 歌曲id
	 * @return 布尔变量，表示成功或失败
	 */
	@RequestMapping(value = "/uploadLyrics", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadlyrics(HttpServletRequest request, int id) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext()
        );
		if(multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			Iterator<?> iter = multipartHttpServletRequest.getFileNames();
            if (iter.hasNext()) {
            	String fileName = iter.next().toString();
            	MultipartFile file = multipartHttpServletRequest.getFile(fileName);
            	if (file != null) {
            		fileName = file.getOriginalFilename();
            		Song song = songService.getInfo(id);
            		String prefix = WebInfoPath + "/lyrics/" + song.getArtistName() + "/" + song.getAlbumName() + "/";//获取歌词路径
            		String filePath = prefix + fileName;
            		try {
            			//将文件存储在硬盘上
						file.transferTo(new File(filePath));
					} catch (IllegalStateException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
            		songService.setLyricsPath(id, filePath);
            	}
            }
		}
		return true;
	}

	/**
	 * 管理员上传歌曲音频文件
	 * 
	 * @param request 包含歌词文件的http request
	 * @param id
	 * @return 布尔变量，表示成功或失败
	 */
	@RequestMapping(value = "/uploadSongFile", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadSong(HttpServletRequest request, int id) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext()
        );
		if(multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			Iterator<?> iter = multipartHttpServletRequest.getFileNames();
            if (iter.hasNext()) {
            	String fileName = iter.next().toString();
            	MultipartFile file = multipartHttpServletRequest.getFile(fileName);//从request中获取文件
            	if (file != null) {
            		fileName = file.getOriginalFilename();
            		Song song = songService.getInfo(id);
            		String prefix = WebInfoPath + "/music/" + song.getArtistName() + "/" + song.getAlbumName() + "/";//获取歌曲音频文件路径
            		String filePath = prefix + fileName;
            		try {
            			//将文件存储在硬盘上
						file.transferTo(new File(filePath));
					} catch (IllegalStateException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
            		songService.setFilePath(id, filePath);
            	}
            }
		}
		return true;
	}
	
	@RequestMapping(value = "/uploadSongImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadSongImage(CommonsMultipartFile file, int id) {
		String fileName = file.getOriginalFilename();
		Song song = songService.getInfo(id);
		String prefix = WebInfoPath + "/image/song/" + song.getArtistName() + "/" + song.getAlbumName() + "/";;//获取歌曲图片路径
		String filePath = prefix + fileName;
		avatar_file = new File(filePath);
		try {
			file.transferTo(avatar_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		songService.setImage(id, filePath);
		return true;
	}

	@RequestMapping(value = "/uploadPlaylistImage", method = RequestMethod.GET)
	@ResponseBody
	public Boolean uploadPlaylistImage(HttpSession session, CommonsMultipartFile file, int id) {
		String fileName = file.getOriginalFilename();
		Playlist playlist = playlistService.getInfo(id);
		String userId = (String)session.getAttribute(UserConstant.USER_ID);
		String prefix = WebInfoPath + "/image/user/" + playlist;//获取歌单图片路径
		String filePath = prefix + fileName;
		avatar_file = new File(filePath);
		try {
			file.transferTo(avatar_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		playlistService.setImage(id, filePath);
		return true;
	}
	
	/**
	 * 裁剪头像
	 * 
	 * @param imagePath 
	 * @throws IOException
	 */
	private void cut(String imagePath) throws IOException {

        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            // 读取图片文件
            is = new FileInputStream(avatar_file);

            /*
             *
             * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
             *
             * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
             *
             * (例如 "jpeg" 或 "tiff")等 。
             */

            String prefix = imagePath.substring(imagePath
                    .lastIndexOf(".") + 1);
            Iterator<ImageReader> it = ImageIO
                    .getImageReadersByFormatName(prefix);

            ImageReader reader = it.next();

            // 获取图片流
            iis = ImageIO.createImageInputStream(is);

            /*
             *
             * <p>
             * iis读取源。true只向前搜索
             * </p>
             * .将它标记为 ‘只向前搜索’。
             *
             * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
             *
             * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
             */
            reader.setInput(iis, true);

            /*
             *
             * <p>
             * 描述如何对流进行解码的类
             * <p>
             * .用于指定如何在输入时从 Java Image I/O
             *
             * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
             *
             * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回
             *
             * ImageReadParam 的实例。
             */
            ImageReadParam param = reader.getDefaultReadParam();

            /*
             *
             * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
             *
             * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
             */

            int x = (int) headPicAttr.getX();
            int y = (int) headPicAttr.getY();
            int width = (int) headPicAttr.getWidth();
            int height = (int) headPicAttr.getHeight();
            Rectangle rect = new Rectangle(x, y, width, height);

            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);

            /*
             *
             * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
             *
             * 它作为一个完整的 BufferedImage 返回。
             */
            BufferedImage bi = reader.read(0, param);

            // 保存新图片
            ImageIO.write(bi, prefix, new File(imagePath));
        } finally {
            if (is != null)
                is.close();
            if (iis != null)
                iis.close();
        }
    }
	
}
