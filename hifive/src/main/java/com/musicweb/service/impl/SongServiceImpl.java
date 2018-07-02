package com.musicweb.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.ListModel;

import org.springframework.stereotype.Service;

import com.musicweb.constant.DisplayConstant;
import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.SongService;
import com.musicweb.util.DurationUtil;
import com.musicweb.util.FileUtil;
import com.musicweb.util.RedisUtil;

/**
 * 歌曲模块业务逻辑实现类
 * 
 * @author brian
 * 
 */
@Service("songService")
public class SongServiceImpl implements SongService {
	
	@Resource
	private SongDao songDao;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private RedisUtil redisUtil;
	@Resource
	private CacheServiceImpl cacheService;

	/**
	 * @see SongService#search(String, int)
	 */
	@Override
	public List<Song> search(String name, int page) {
		int count = DisplayConstant.SEARCH_PAGE_SONG_SIZE;//每页显示多少条搜索结果
		List<Song>songs = songDao.selectByName(name, (page-1)*count, count);
		for(Song song: songs) {
			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
			String filePath = WebInfoPath + song.getFilePath();
			song.setDuration(DurationUtil.computeDuration(filePath));
		}
		return songs;
	}

	/**
	 * @see SongService#lookUpRank(int, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Song> lookUpRank(int type, boolean isAll) {
		Object songs = redisUtil.hget("rank", String.valueOf(type));//先查找缓存中有没有对应的排行榜，还要看isAll
		if(songs == null) {
			int count = DisplayConstant.RANK_PAGE_RANK_SIZE;//显示排行榜的页面展示多少首歌曲
			switch(type) {// 根据type选择不同的dao方法
			case 1:
				songs = songDao.selectLatest(0, count);
				break;
			case 2:
				songs = songDao.selectHittest(count);
				break;
			case 3:
				songs = songDao.selectRankByRegion(1, count);
				break;
			case 4:
				songs = songDao.selectRankByRegion(2, count);
				break;
			case 5:
				songs = songDao.selectRankByRegion(4, count);
				break;
			case 6:
				songs = songDao.selectRankByRegion(3, count);
				break;
			default:
				break;
			}
			if(songs != null) {
				for(Song song: (List<Song>)songs) {
					String classPath = this.getClass().getClassLoader().getResource("").getPath();
					String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
					String filePath = WebInfoPath + song.getFilePath();
					song.setDuration(DurationUtil.computeDuration(filePath));
					if(song.getImage() == null)
						song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
				}
				redisUtil.hset("rank", String.valueOf(type), songs, TimeConstant.A_DAY);//将排行榜放进缓存
			}
		}
		if (!isAll) {
			List<Song> songsForRank = (List<Song>)songs;
			List<Song> songsForHome = new ArrayList<>();
			for(int i = 0; i < DisplayConstant.HOME_PAGE_RANK_SIZE && i < songsForRank.size(); i++)
				songsForHome.add(songsForRank.get(i));
			return songsForHome;
		}
		return (List<Song>)songs;
	}

	/**
	 * @see SongService#getInfo(int)
	 */
	@Override
	public Song getInfo(int id) {
		Song song = cacheService.getAndCacheSongBySongID(id);
		return song;
	}

	/**
	 * @see SongService#play(int)
	 */
	@Override
	public void play(int id) {
		//缓存中没有歌曲播放量的话就去数据库拿歌曲对象，增加播放量，再把歌曲和歌曲播放量都放进缓存中
		//缓存中有歌曲播放量的话就把缓存中的播放量加一
		Object object = redisUtil.hget("song_play_count", String.valueOf(id));
		//System.out.println("歌曲" + id + "在缓存中的播放量为：" + object);
		int albumId = 0;//该歌曲所属专辑的id
		int artistId = 0;//该歌曲所属歌手的id
		//缓存中没有歌曲播放量
		if(object == null) {
			Song song = songDao.selectById(id);
			song.setPlayCount(song.getPlayCount()+1);
			//System.out.println("歌曲的播放量为：" + song.getPlayCount());
			redisUtil.hset("song", String.valueOf(id), song, TimeConstant.A_DAY);//把歌曲放进缓存中
			redisUtil.hset("song_play_count", String.valueOf(id), song.getPlayCount());//把歌曲播放量放进缓存中
			//System.out.println(redisUtil.hget("song_play_count", String.valueOf(id)));
			albumId = song.getAlbumId();
			//System.out.println("专辑id：" + albumId);
			artistId = song.getArtistId();
			//System.out.println("歌手id：" + artistId);
		}
		else
			redisUtil.hincr("song_play_count", String.valueOf(id), 1);//缓存中有歌曲播放量
		//增加该歌曲所属专辑的播放量
		object = redisUtil.hget("album_play_count", String.valueOf(albumId));
		//缓存中没有该歌曲所属专辑的播放量
		if(object == null) {
			Album album = albumDao.select(albumId);
			album.setPlayCount(album.getPlayCount()+1);
			//System.out.println("专辑的播放量为：" + album.getPlayCount());
			redisUtil.hset("album", String.valueOf(albumId), album, TimeConstant.A_DAY);//把该歌曲所属专辑放进缓存中
			redisUtil.hset("album_play_count", String.valueOf(albumId), album.getPlayCount());//把该歌曲所属专辑的播放量放进缓存中
			//System.out.println(redisUtil.hget("album_play_count", String.valueOf(albumId)));
		}
		else
			redisUtil.hincr("album_play_count", String.valueOf(albumId), 1);//缓存中有该歌曲所属专辑的播放量
		//增加该歌曲所属歌手的播放量
		object = redisUtil.hget("artist_play_count", String.valueOf(artistId));
		//缓存中没有歌曲播放量
		if(object == null) {
			Artist artist = artistDao.select(artistId);
			artist.setPlayCount(artist.getPlayCount()+1);
			//System.out.println("歌手的播放量为：" + artist.getPlayCount());
			redisUtil.hset("artist", String.valueOf(artistId), artist, TimeConstant.A_DAY);//把该歌曲所属歌手放进缓存中
			redisUtil.hset("artist_play_count", String.valueOf(artistId), artist.getPlayCount());//把该歌曲所属歌手的播放量放进缓存中
			//System.out.println(redisUtil.hget("artist_play_count", String.valueOf(artistId)));
		}
		else
			redisUtil.hincr("album_play_count", String.valueOf(artistId), 1);//缓存中有该歌曲所属歌手的播放量
	}

	/**
	 * @see SongService#add(Song)
	 */
	@Override
	public int add(Song song) {
		int i = songDao.insert(song);//返回歌曲id
		if(i == 0)
			return -1;
		int id = song.getId();
		redisUtil.hset("song", String.valueOf(id), song, TimeConstant.A_DAY);//专辑的歌曲列表，歌手的专辑列表都没有缓存
		//删除相关缓存
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		return id;
	}

	/**
	 * @see SongService#remove(int)
	 */
	@Override
	public boolean remove(int id) {
		Song song = null;
		String songPath = null;
		String lyricsPath = null;
		String imagePath = null;
		//删除缓存
		if((song = (Song)redisUtil.hget("song", String.valueOf(id))) !=null) {
			redisUtil.hdel("song", String.valueOf(id));
		}
		else {
			song = songDao.selectById(id);
		}
		if(song == null)
			return false;
		//拼接出歌曲文件夹路径，删除整个文件夹
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
		//删除歌曲图片
		imagePath = WebInfoPath + song.getImage();
		System.out.println(imagePath);
		FileUtil.deleteFile(new File(imagePath));
		//删除歌词文件
		lyricsPath = WebInfoPath + song.getLyricsPath();
		FileUtil.deleteFile(new File(lyricsPath));
		//删除歌曲音频文件
		songPath = WebInfoPath + song.getFilePath();
		FileUtil.deleteFile(new File(songPath));
		//删除数据库中的歌曲记录
		songDao.delete(id);
		//删除相关缓存
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.hdel("song_play_count", String.valueOf(id));
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		//删除歌单-歌曲表中对应的记录（playlistdao已经修改），要这样做吗？用户不知不觉发现自己收藏的歌不见了。
		return true;
	}

	/**
	 * @see SongService#modify(Song)
	 */
	@Override
	public boolean modify(Song song) {
		Song oldSong = cacheService.getAndCacheSongBySongID(song.getId());
		song.setAlbumId(oldSong.getAlbumId());
		song.setArtistId(song.getArtistId());
		int i = songDao.update(song);//前端把歌曲id也一起发过来了
		redisUtil.hdel("song", String.valueOf(song.getId()));//删除旧歌曲的缓存
		cacheService.getAndCacheSongBySongID(song.getId());
		//删除相关缓存
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}

	/**
	 * @see SongService#setImage(int, String)
	 */
	@Override
	public boolean setImage(int id, String image) {
		int i = songDao.updateImage(id, image);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSongBySongID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}

	/**
	 * @see SongService#lookUpNewSongs(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Song> lookUpNewSongs(int region) {//有问题，新歌还有分地区
		Object object = redisUtil.hget("new_song", String.valueOf(region));
		if(object == null) {
			List<Song> songs = null;
			songs = songDao.selectLatest(region, DisplayConstant.HOME_PAGE_NEW_SONG_SIZE);//待改
			for(Song song: songs) {
				String classPath = this.getClass().getClassLoader().getResource("").getPath();
				String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));
				String filePath = WebInfoPath + song.getFilePath();
				song.setDuration(DurationUtil.computeDuration(filePath));
				if(song.getImage() == null)
					song.setImage(cacheService.getAndCacheAlbumByAlbumID(song.getAlbumId()).getImage());
			}
			redisUtil.hset("new_song", String.valueOf(region), songs, TimeConstant.A_DAY);
			for(Song song: songs) {
				redisUtil.hset("song", String.valueOf(song.getId()), song, TimeConstant.A_DAY);
			}
			return songs;
		} else {
			return (List<Song>)object;
		}
		

	}

	/**
	 * @see SongService#getSearchCount(String)
	 */
	@Override
	public int getSearchCount(String name) {
		int count = songDao.selectCountByName(name);
		return count;
	}

	/**
	 * @see SongService#getDuration(int)
	 */
	@Override
	public String getDuration(int id) {
		Song song = songDao.selectById(id);
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf("/classes"));////;
		String filePath = WebInfoPath + song.getFilePath();
		String duration = DurationUtil.computeDuration(filePath);
		return duration;
	}

	/**
	 * @see SongService#setLyricsPath(int, String)
	 */
	@Override
	public boolean setLyricsPath(int id, String lyricsPath) {
		int i = songDao.updateLyricsPath(id, lyricsPath);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSongBySongID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}

	/**
	 * @see SongService#setFilePath(int, String)
	 */
	@Override
	public boolean setFilePath(int id, String filePath) {
		int i = songDao.updateFilePath(id, filePath);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSongBySongID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}
	
}
