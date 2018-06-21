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

import com.musicweb.util.FileUtil;

import com.musicweb.constant.DisplayConstant;
import com.musicweb.constant.TimeConstant;
import com.musicweb.service.impl.CacheServiceImpl;

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

	@Override
	public List<Artist> search(String name, int page) {
		name.trim();
		int num = DisplayConstant.SEARCH_PAGE_SINGER_SIZE;
		List<Artist> artistList = artistDao.selectByName(name, (page - 1) * num, num);
		Collections.sort(artistList, new Comparator<Artist>() {
			@Override
			public int compare(Artist o1, Artist o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return artistList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page) {
		initial.trim();
		Object object = redisUtil.hget("artist_filter", initial + "_" + region + "_" + gender + "_" + page);
		List<Artist> artistList;
		if (object == null) {
			int num = DisplayConstant.SEARCH_PAGE_SINGER_SIZE;
			artistList = artistDao.selectByCategory(initial, region, gender, (page - 1) * num, num);
			if (artistList != null) {
				redisUtil.hset("artist_filter", initial + "_" + region + "_" + gender + "_" + page, artistList,
						TimeConstant.A_DAY);
			}
		}
		else artistList = (List<Artist>) object;
		Collections.sort(artistList, new Comparator<Artist>() {
			@Override
			public int compare(Artist o1, Artist o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return artistList;
	}

	@Override
	public Artist getInfo(int id) {
		Artist artist = cacheService.getAndCacheSingerBySingerID(id);
		return artist;
	}

	@Override
	public int add(Artist artist) {
		artist.setBirthplace(artist.getBirthplace().trim());
		artist.setCountry(artist.getCountry().trim());
		artist.setImage(artist.getImage().trim());
		artist.setInitial(artist.getInitial().trim());
		artist.setIntro(artist.getIntro().trim());
		artist.setName(artist.getName().trim());
		artist.setRepresentative(artist.getRepresentative().trim());
		artist.setOccupation(artist.getOccupation().trim());
		
		int id = artistDao.insert(artist);
		redisUtil.hset("artist", String.valueOf(id), artist, TimeConstant.A_DAY);
		return id;
	}

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
		String WebInfoPath = classPath.substring(0, classPath.indexOf(FileUtil.FILE_SEPARATOR + "classes"));
		
		List<Album> albumList = lookUpAlbumsByArtist(id);
		for(Album album:albumList) {
			List<Song> songList = albumDao.selectAllSongs(album.getId());
			//删音乐文件
			if(songList != null) {
				//删除歌曲图片
				Song firstSong = songList.get(0);
				String songImageFolderPath = WebInfoPath + "/image/song/" + firstSong.getArtistName() + "/" + firstSong.getAlbumName();
				FileUtil.deleteFolder(new File(songImageFolderPath));
			
				//删除歌词
				String lyricsPath = songList.get(0).getLyricsPath();
				String lyricsFolderPath = WebInfoPath + lyricsPath.substring(0, lyricsPath.lastIndexOf('/'));
				FileUtil.deleteFolder(new File(lyricsFolderPath));
			
				//删除音乐文件
				String musicPath = songList.get(0).getFilePath();
				String musicFolderPath = WebInfoPath + musicPath.substring(0, lyricsPath.lastIndexOf('/'));
				FileUtil.deleteFolder(new File(musicFolderPath));
			}
			//删音乐缓存、数据库
			for(Song song: songList) {
				redisUtil.hdel("song", String.valueOf(song.getId()));
				playlistDao.deleteSongInAll(song.getId());
				userDao.deleteLikeSongInAll(song.getId());
				songDao.delete(song.getId());
			}
			//删专辑缓存、数据库
			redisUtil.hdel("album", String.valueOf(album.getId()));
			redisUtil.hdel("album_songs", String.valueOf(album.getId()));
			userDao.deleteLikeAlbumInAll(album.getId());
			albumDao.delete(album.getId());
			//删专辑图片
			String albumImageFilePath = WebInfoPath + album.getImage();
			FileUtil.deleteFile(new File(albumImageFilePath));
		}

		//删歌手数据库、文件
		artistDao.delete(id);
		String artistImageFilePath = WebInfoPath + artist.getImage();
		FileUtil.deleteFile(new File(artistImageFilePath));
		
		return true;
	}

	@Override
	public boolean modify(Artist artist) {
		artist.setBirthplace(artist.getBirthplace().trim());
		artist.setCountry(artist.getCountry().trim());
		artist.setImage(artist.getImage().trim());
		artist.setInitial(artist.getInitial().trim());
		artist.setIntro(artist.getIntro().trim());
		artist.setName(artist.getName().trim());
		artist.setRepresentative(artist.getRepresentative().trim());
		artist.setOccupation(artist.getOccupation().trim());
		
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
			
			List<Album> albumList = lookUpAlbumsByArtist(artist.getId());
			for(Album album:albumList) {
				album.setArtistName(artist.getName());
				int albumId = album.getId();
				redisUtil.hdel("album", String.valueOf(albumId));
				redisUtil.hset("album", String.valueOf(albumId), album);
				redisUtil.hdel("album_songs", String.valueOf(albumId));
				List<Song> songsInAlbumList = albumDao.selectAllSongs(albumId);
				for(Song song: songsInAlbumList){
					song.setArtistName(artist.getName());
					redisUtil.hdel("song", String.valueOf(song.getId()));
					redisUtil.hset("song",String.valueOf(song.getId()),song);
				}
				redisUtil.hset("album_songs", String.valueOf(albumId), songsInAlbumList);
 			}
			redisUtil.hdel("artist_albums", String.valueOf(artist.getId()));
			redisUtil.hset("artist_albums", String.valueOf(artist.getId()), albumList);
		}
		
		artistDao.update(artist);
		redisUtil.hdel("artist", String.valueOf(artist.getId()));
		redisUtil.hset("artist", String.valueOf(artist.getId()), artist, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean setImage(int id, String image) {
		image.trim();
		artistDao.updateImage(id, image);
		redisUtil.hdel("artist", String.valueOf(id));
		cacheService.getAndCacheSingerBySingerID(id);
		redisUtil.del("artist_filter");
		redisUtil.del("artist_filter_count");
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		String WebInfoPath = classPath.substring(0, classPath.indexOf(FileUtil.FILE_SEPARATOR + "classes"));
		String artistImageFilePath = WebInfoPath + image;
		FileUtil.deleteFile(new File(artistImageFilePath));
		return true;
	}

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
				songList.add(song);
			}
			redisUtil.hset("album_songs", String.valueOf(album.getId()), songsInAlbumList);
		}
		Collections.sort(songList, new Comparator<Song>() {
			@Override
			public int compare(Song o1, Song o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		//返回歌手的歌曲列表
		return songList;
	}

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
		Collections.sort(albumList, new Comparator<Album>() {
			@Override
			public int compare(Album o1, Album o2) {
				return o2.getPlayCount() - o1.getPlayCount();
			}
		});
		return albumList;
	}

	@Override
	public int getSearchCount(String name) {
		name.trim();
		return artistDao.selectCountByName(name);
	}

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
