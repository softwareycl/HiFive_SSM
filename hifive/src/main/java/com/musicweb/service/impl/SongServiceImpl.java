package com.musicweb.service.impl;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
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
import com.musicweb.util.DeleteFileUtil;
import com.musicweb.util.RedisUtil;

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

	@Override
	public List<Song> search(String name, int page) {
		int count = DisplayConstant.SEARCH_PAGE_SONG_SIZE;
		List<Song>songs = songDao.selectByName(name, (page-1)*count, count);
		return songs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Song> lookUpRank(int type, boolean isAll) {
		// 根据type选择不同的dao方法
		//type的取值和对应的类型有待确定
		//缓存排行榜
		Object songs = redisUtil.hget("rank", String.valueOf(type));
		if(songs == null) {
			int count = isAll?20:4;
			switch(type) {
			case 1:
				songs = songDao.selectLatest(count);
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
				redisUtil.hset("rank", String.valueOf(type), songs, TimeConstant.A_DAY);//time???
			}
		}
		return (List<Song>)songs;
	}

	@Override
	public Song getInfo(int id) {
		Song song = cacheService.getAndCacheSongBySongID(id);
		return song;
	}

	@Override
	public void play(int id) {
		//缓存中没有歌曲播放量的话就去数据库拿歌曲对象，增加播放量，再把歌曲和歌曲播放量都放进缓存中
		//缓存中有歌曲播放量的话就把缓存中的播放量加一，重新放进缓冲中
		Object object = redisUtil.hget("song_play_count", String.valueOf(id));
		int albumId = 0;
		int artistId = 0;
		if(object == null) {
			Song song = songDao.selectById(id);
			song.setPlayCount(song.getPlayCount()+1);
			redisUtil.hset("song", String.valueOf(id), song, TimeConstant.A_DAY);
			redisUtil.hset("song_play_count", String.valueOf(id), song.getPlayCount());
			albumId = song.getAlbumId();
			artistId = song.getArtistId();
		}
		redisUtil.hincr("song_play_count", String.valueOf(id), 1);
		//专辑播放量
		object = redisUtil.hget("album_play_count", String.valueOf(albumId));
		if(object == null) {
			Album album = albumDao.select(albumId);
			album.setPlayCount(album.getPlayCount()+1);
			redisUtil.hset("song", String.valueOf(id), album, TimeConstant.A_DAY);
			redisUtil.hset("song_play_count", String.valueOf(id), album.getPlayCount());
		}
		redisUtil.hincr("album_play_count", String.valueOf(albumId), 1);
		//歌手播放量
		object = redisUtil.hget("artist_play_count", String.valueOf(artistId));
		if(object == null) {
			Artist artist = artistDao.select(artistId);
			artist.setPlayCount(artist.getPlayCount()+1);
			redisUtil.hset("song", String.valueOf(id), artist, TimeConstant.A_DAY);
			redisUtil.hset("song_play_count", String.valueOf(id), artist.getPlayCount());
		}
		redisUtil.hincr("album_play_count", String.valueOf(artistId), 1);
	}

	@Override
	public int add(Song song) {
		int id = songDao.insert(song);
		redisUtil.hset("song", String.valueOf(id), song, TimeConstant.A_DAY);//专辑的歌曲列表，歌手的专辑列表都没有缓存
		//删除相关缓存
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		return id;
	}

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
		//歌曲一定存在
		songPath = song.getFilePath();
		lyricsPath = song.getLyricsPath();
		imagePath = song.getImage();
		//删除歌曲文件，歌词文件，歌曲图片（如果有的话）
		if(imagePath != null) {//不是专辑图片则需要删除歌曲图片文件。这样判断正确吗？在获取歌曲的时候不要填入专辑图片
			DeleteFileUtil.delete(imagePath);
		}
		DeleteFileUtil.delete(songPath);
		DeleteFileUtil.delete(lyricsPath);
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

	@Override
	public boolean modify(Song song) {
		songDao.update(song);//前端把歌曲id也一起发过来了
		redisUtil.hdel("song", String.valueOf(song.getId()));
		cacheService.getAndCacheSingerBySingerID(song.getId());
		//删除相关缓存
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return false;
	}

	@Override
	public boolean setImage(int id, String image) {
		int i = songDao.updateImage(id, image);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}

	@Override
	public List<Song> lookUpNewSongs(int region) {//有问题，新歌还有分地区
		List<Song> songs = null;
		songs = songDao.selectLatest(20);//待改
		return songs;
	}

	@Override
	public int getSearchCount(String name) {
		int count = songDao.selectCountByName(name);
		return count;
	}

	@Override
	public String getDuration(int id) {
//		Song song = songDao.selectById(id);
//		String path = song.getFilePath();
		File file = new File("/home/brian/eclipse/eclipse-workspace/MusicWeb/src/main/webapp/WEB-INF/music/Alan Walker/Faded/Faded.mp3");
		try {
			MP3File f = (MP3File)AudioFileIO.read(file);
			MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
			System.out.println(audioHeader.getTrackLength());//以秒为单位
			int len = audioHeader.getTrackLength();
			int minute = len/60;
			int second = len%60;
			String format = "%02d";
			String minutes = String.format(format, minute);
			String seconds = String.format(format, second);
			String duration = minutes + ": " + seconds;
			return duration;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean setLyricsPath(int id, String lyricsPath) {
		int i = songDao.updateLyricsPath(id, lyricsPath);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}

	@Override
	public boolean setFilePath(int id, String filePath) {
		int i = songDao.updateFilePath(id, filePath);
		//删除相关缓存
		redisUtil.hdel("song", String.valueOf(id));
		cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.del("album_songs");
		redisUtil.del("rank");
		redisUtil.del("new_song");
		redisUtil.del("user_songs");
		redisUtil.del("playlist_songs");
		return i>0;
	}
	
	public static void main(String[] args) {
		SongService service = new SongServiceImpl();
		System.out.println(service.getDuration(1));
	}

}
