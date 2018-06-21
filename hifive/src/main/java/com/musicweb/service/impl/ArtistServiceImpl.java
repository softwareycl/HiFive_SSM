package com.musicweb.service.impl;

import java.util.ArrayList;
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
		return artistList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page) {
		initial.trim();
		Object object = redisUtil.hget("artist_filter", initial + "_" + region + "_" + gender + "_" + page);
		if (object == null) {
			int num = DisplayConstant.SEARCH_PAGE_SINGER_SIZE;
			List<Artist> artistList = artistDao.selectByCategory(initial, region, gender, (page - 1) * num, num);
			if (artistList != null) {
				redisUtil.hset("artist_filter", initial + "_" + region + "_" + gender + "_" + page, artistList,
						TimeConstant.A_DAY);
			}
			return artistList;
		}
		return (List<Artist>) object;
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
		
		List<Song> songList = lookUpSongsByArtist(id);
		for(Song song: songList) {
			redisUtil.hdel("song", String.valueOf(song.getId()));
			playlistDao.deleteSongInAll(song.getId());
			userDao.deleteLikeSongInAll(song.getId());
			songDao.delete(song.getId());
		}
		List<Album> albumList = lookUpAlbumsByArtist(id);
		for(Album album:albumList) {
			redisUtil.hdel("album", String.valueOf(album.getId()));
			redisUtil.hdel("album_songs", String.valueOf(album.getId()));
			userDao.deleteLikeAlbumInAll(album.getId());
			albumDao.delete(album.getId());
		}

		//删数据库
		artistDao.delete(id);
		
		//删除文件夹 TODO
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
		Object object = redisUtil.hget("artist", String.valueOf(artist.getId()));
		Artist artistOld = new Artist();
		if(object == null) 
			artistOld = artistDao.select(artist.getId());
		else 
			artistOld = (Artist) object;
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
		return artistDao.updateImage(id, image) > 0 ? true : false;
	}

	@Override
	public List<Song> lookUpSongsByArtist(int id) {
		Object object = redisUtil.hget("artist_albums", String.valueOf(id));
		if (object == null) {
			List<Album> albumList = artistDao.selectAllAlbums(id);
			if (albumList == null) {
				return null;
			}
			redisUtil.hset("artist_albums", String.valueOf(id), albumList, TimeConstant.A_DAY);
		}
		@SuppressWarnings("unchecked")
		List<Album> albumList = (List<Album>) object;
		ArrayList<Song> songList = new ArrayList<Song>();
		for (Album album : albumList) {
			List<Song> songInAlbumList = albumDao.selectAllSongs(album.getId());
			for (Song song : songInAlbumList) {
				songList.add(song);
			}
		}
		return songList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Album> lookUpAlbumsByArtist(int id) {
		Object object = redisUtil.hget("artist_albums", String.valueOf(id));
		if (object == null) {
			List<Album> albumList = artistDao.selectAllAlbums(id);
			if (albumList != null) {
				redisUtil.hset("artist_albums", String.valueOf(id), albumList, TimeConstant.A_DAY);
			}
			return albumList;
		}
		return (List<Album>) object;
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
