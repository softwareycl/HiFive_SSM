package com.musicweb.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.dao.UserDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.ArtistService;
import com.musicweb.util.RedisUtil;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.FileUtil;

import com.musicweb.constant.DisplayConstant;
import com.musicweb.constant.TimeConstant;
import com.musicweb.service.impl.CacheServiceImpl;

/**
 * ArtistServiceImpl
 * @author likexin
 * @Date 2018.6.21
 * ArtistServiceImpl完成有关歌手模块的业务逻辑实现
 * 接受ArtistController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给ArtistController
 * Service层针对业务数据增加各类缓存操作
 */
@Service("artistService")
public class ArtistServiceImpl implements ArtistService {

	@Resource
	private ArtistDao artistDao;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private UserDao userDao;
	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private SongDao songDao;
	@Resource
	private RedisUtil redisUtil;
	@Resource
	private CacheServiceImpl cacheService;

	/**
	 * 以名字为关键字搜索歌手
	 * @param name 歌手名字
	 * @param page 目标页码
	 * @return List<Artist> 歌手列表
	 */
	@Override
	public List<Artist> search(String name, int page) {
		name.trim();
		int num = DisplayConstant.SEARCH_PAGE_SINGER_SIZE;
		// (page - 1) * num 为起始位置， num 为偏移量
		List<Artist> artistList = artistDao.selectByName(name, (page - 1) * num, num);
		//得到的歌手列表根据歌手的playCount进行降序排序
		Collections.sort(artistList, new Comparator<Artist>() {
			@Override
			public int compare(Artist o1, Artist o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return artistList;
	}

	/**
	 * 根据首字母，地区，性别类别筛选歌手
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @param page 目标页码
	 * @return 歌手列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page) {
		initial.trim();
		//先查缓存
		Object object = redisUtil.hget("artist_filter", initial + "_" + region + "_" + gender + "_" + page);
		List<Artist> artistList;
		if (object == null) {
			int num = DisplayConstant.SIGNER_PAGE_SINGER_SIZE;
			artistList = artistDao.selectByCategory(initial, region, gender, (page - 1) * num, num);
			if (artistList != null) {
				redisUtil.hset("artist_filter", initial + "_" + region + "_" + gender + "_" + page, artistList,
						TimeConstant.A_DAY);
			}
		}
		else artistList = (List<Artist>) object;
		//得到的歌手列表根据歌手的playCount进行降序排序
		Collections.sort(artistList, new Comparator<Artist>() {
			@Override
			public int compare(Artist o1, Artist o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return artistList;
	}

	/**
	 * 显示歌手详情
	 * @param id 歌手id
	 * @return 歌手
	 */
	@Override
	public Artist getInfo(int id) {
		Artist artist = cacheService.getAndCacheSingerBySingerID(id);
		return artist;
	}

	/**
	 * 添加歌手
	 * @param artist 歌手
	 * @return 新增的歌手ID
	 */
	@Override
	public int add(Artist artist) {
		//对String值属性进行预处理
		artist.setBirthplace(artist.getBirthplace().trim());
		artist.setCountry(artist.getCountry().trim());
		artist.setInitial(artist.getInitial().trim());
		
		artist.setName(artist.getName().trim());
		if(artist.getRepresentative() != null)
			artist.setRepresentative(artist.getRepresentative().trim());
		if(artist.getOccupation() != null)
			artist.setOccupation(artist.getOccupation().trim());
		if(artist.getRepresentative() != null)
			artist.setIntro(artist.getIntro().trim());
		
		redisUtil.del("artist_filter");
		redisUtil.del("artist_filter_count");
		
		int id = artistDao.insert(artist);
		redisUtil.hset("artist", String.valueOf(id), artist, TimeConstant.A_DAY);
		return id;
	}

	/**
	 * 删除歌手
	 * @param id 歌手ID
	 * @return 操作状态
	 */
	@Override
	public boolean remove(int id) {
		//删缓存
		Artist artist = cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.hdel("artist", String.valueOf(id));
		redisUtil.del("playlist");
		redisUtil.del("artist_albums");
		redisUtil.del("album_filter");
		redisUtil.del("artist_filter");
		redisUtil.del("album_filter_count");
		redisUtil.del("artist_filter_count");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("new_album");
		redisUtil.del("song_play_count");
		redisUtil.del("album_play_count");
		redisUtil.del("artist_play_count");
		redisUtil.del("user_songs");
		redisUtil.del("user_albums");
		redisUtil.del("playlist_songs");
		
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		
		List<Album> albumList = lookUpAlbumsByArtist(id);
		for(Album album:albumList) {
			List<Song> songList = albumDao.selectAllSongs(album.getId());
			//删音乐文件
			if(songList != null && songList.size() > 0) {
				//删除歌曲图片
				String songImageFolderPath = WebInfoPath + "/image/song/" + artist.getName();
				FileUtil.deleteFolder(new File(songImageFolderPath));
			
				//删除歌词
				String lyricsFolderPath = WebInfoPath + "/lyrics/" + artist.getName();
				FileUtil.deleteFolder(new File(lyricsFolderPath));
			
				//删除音乐文件
				String musicFolderPath = WebInfoPath + "/music/" + artist.getName();
				FileUtil.deleteFolder(new File(musicFolderPath));
			}
			//删音乐缓存、数据库
			for(Song song: songList) {
				redisUtil.hdel("song", String.valueOf(song.getId()));
			}
			//删专辑缓存、数据库
			redisUtil.hdel("album", String.valueOf(album.getId()));
			redisUtil.hdel("album_songs", String.valueOf(album.getId()));
			//删专辑图片
			if(album.getImage() != null) {
				String albumImageFilePath = WebInfoPath + album.getImage();
				FileUtil.deleteFile(new File(albumImageFilePath));
			}

		}

		//删歌手数据库、文件
		artistDao.delete(id);
		if(artist.getImage() != null) {
			String artistImageFilePath = WebInfoPath + artist.getImage();
			FileUtil.deleteFile(new File(artistImageFilePath));
		}

		
		return true;
	}

	/**
	 * 修改歌手信息
	 * @param artist 歌手
	 * @return 操作状态
	 */
	@Override
	public boolean modify(Artist artist) {
		
		//artist名称变化
		Artist artistOld = cacheService.getAndCacheSingerBySingerID(artist.getId());
		if(!artistOld.getName().equals(artist.getName())) {
			redisUtil.del("album_filter");
			redisUtil.del("album_filter_count");
			redisUtil.del("artist_filter");
			redisUtil.del("artist_filter_count");
			redisUtil.del("rank");
			redisUtil.del("new_song");
			redisUtil.del("user_songs");
			redisUtil.del("user_albums");
			redisUtil.del("playlist_songs");
			
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			
			//更新缓存
			List<Album> albumList = lookUpAlbumsByArtist(artist.getId());
			for(Album album:albumList) {
				album.setArtistName(artist.getName());
				int albumId = album.getId();
				redisUtil.hset("album", String.valueOf(albumId), album, TimeConstant.A_DAY);
				List<Song> songsInAlbumList = albumDao.selectAllSongs(albumId);
				for(Song song: songsInAlbumList){
					song.setArtistName(artist.getName());
					//设置歌曲图片
					if(song.getImage() == null)
						song.setImage(cacheService.getAndCacheAlbumByAlbumID(albumId).getImage());
					//设置歌曲时长
					String musicFilePath = WebInfoPath + song.getFilePath();
					song.setDuration(DurationUtil.computeDuration(musicFilePath));
					redisUtil.hset("song",String.valueOf(song.getId()),song, TimeConstant.A_DAY);
				}
				redisUtil.hset("album_songs", String.valueOf(albumId), songsInAlbumList, TimeConstant.A_DAY);
 			}
			redisUtil.hset("artist_albums", String.valueOf(artist.getId()), albumList, TimeConstant.A_DAY);
		}
		artist.setImage(artistOld.getImage());
		artistDao.update(artist);
		redisUtil.hset("artist", String.valueOf(artist.getId()), artist, TimeConstant.A_DAY);
		return true;
	}

	/**
	 * 设置歌手图片
	 * @param id 歌手id
	 * @param image 图片路径
	 * @return 操作状态
	 */
	@Override
	public boolean setImage(int id, String image) {
		image.trim();
		String imageOld = cacheService.getAndCacheSingerBySingerID(id).getImage();
		artistDao.updateImage(id, image);
		redisUtil.hdel("artist", String.valueOf(id));
		cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.del("artist_filter");
		redisUtil.del("artist_filter_count");
		if(!image.equals(imageOld)) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			String artistImageFilePath = WebInfoPath + imageOld;
			FileUtil.deleteFile(new File(artistImageFilePath));
		}
		return true;
	}

	/**
	 * 查看歌手的歌曲列表
	 * @param id
	 * @return 歌曲列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Song> lookUpSongsByArtist(int id) {
		//看缓存有没有
		Object object = redisUtil.hget("artist_albums", String.valueOf(id));
		List<Album> albumList; 
		//缓存没有查数据库拿专辑列表
		if (object == null) {
			albumList = artistDao.selectAllAlbums(id);
			//专辑列表为空，则返回歌曲列表为空
			if (albumList == null) {
				return null;
			}
			redisUtil.hset("artist_albums", String.valueOf(id), albumList, TimeConstant.A_DAY);
		}
		else albumList = (List<Album>) object;
		
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		
		//对每张专辑先查缓存
		ArrayList<Song> songList = new ArrayList<Song>();
		for (Album album : albumList) {
			Object cachedSongs = redisUtil.hget("album_songs", String.valueOf(album.getId()));
			List<Song> songsInAlbumList;
			if(cachedSongs == null) {
				songsInAlbumList = albumDao.selectAllSongs(album.getId());
			}
			else songsInAlbumList = (List<Song>)cachedSongs;
	        //每张专辑的歌曲都加到要返回的歌曲列表里
			for (Song song : songsInAlbumList) {
				if(song.getImage() == null) song.setImage(album.getImage());
				//设置歌曲时长
				String musicFilePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(musicFilePath));
				songList.add(song);
			}
			redisUtil.hset("album_songs", String.valueOf(album.getId()), songsInAlbumList, TimeConstant.A_DAY);
		}
		//歌曲列表按播放量排序
		Collections.sort(songList, new Comparator<Song>() {
			@Override
			public int compare(Song o1, Song o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		//返回歌手的歌曲列表
		return songList;
	}

	/**
	 * 查看歌手的所有专辑
	 * @param id 歌手id
	 * @return 专辑列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Album> lookUpAlbumsByArtist(int id) {
		Object object = redisUtil.hget("artist_albums", String.valueOf(id));
		List<Album> albumList;
		if (object == null) {
			albumList = artistDao.selectAllAlbums(id);
			if (albumList != null) {
				redisUtil.hset("artist_albums", String.valueOf(id), albumList, TimeConstant.A_DAY);
			}
		}
		else albumList = (List<Album>) object;
		//专辑列表按播放量排序
		Collections.sort(albumList, new Comparator<Album>() {
			@Override
			public int compare(Album o1, Album o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return albumList;
	}

	/**
	 * 获取搜索结果的记录条数
	 * @param name 搜索关键字
	 * @return 记录数
	 */
	@Override
	public int getSearchCount(String name) {
		name.trim();
		return artistDao.selectCountByName(name);
	}

	/**
	 * 获取筛选后的歌手数目
	 * @param initial 首字母
	 * @param region 地区
	 * @param gender 性别
	 * @return 歌手数目
	 */
	@Override
	public int getFilterCount(String initial, int region, int gender) {
		initial.trim();
		Object object = redisUtil.hget("artist_filter_count", initial+"_"+region+"_"+gender);
		if(object == null) {
			int count = artistDao.selectCountByCategory(initial, region, gender);
			redisUtil.hset("artist_filter_count", initial+"_"+region+"_"+gender, count,TimeConstant.A_DAY);
			return count;
		}
		return (int) object;
	}

}
