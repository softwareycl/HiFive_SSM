package com.musicweb.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
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
	private RedisUtil redisUtil;
	@Resource
	private CacheServiceImpl cacheService;

	@Override
	public List<Artist> search(String name, int page) {
		int num = DisplayConstant.SEARCH_PAGE_SINGER_SIZE;
		List<Artist> artistList = artistDao.selectByName(name, (page - 1) * num, num);
		return artistList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Artist> lookUpArtistsByCatagory(String initial, int region, int gender, int page) {
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
		int id = artistDao.insert(artist);
		redisUtil.hset("artist", String.valueOf(id), artist, TimeConstant.A_DAY);
		return id;
	}

	@Override
	public boolean remove(int id) {
		Object object = redisUtil.hget("artist", String.valueOf(id));
		if(object != null) {
			redisUtil.hdel("artist", String.valueOf(id));
		}
		return artistDao.delete(id) > 0 ? true : false;
	}

	@Override
	public boolean modify(Artist artist) {
		Object object = redisUtil.hget("artist", String.valueOf(artist.getId()));
		boolean modify = artistDao.update(artist) > 0 ? true : false;
		if(object == null) {
			redisUtil.hset("artist", String.valueOf(artist.getId()), artist, TimeConstant.A_DAY);
			return modify;
		}
		redisUtil.hdel("artist", String.valueOf(artist.getId()));
		redisUtil.hset("artist", String.valueOf(artist.getId()), artist, TimeConstant.A_DAY);
		return modify;
	}

	@Override
	public boolean setImage(int id, String image) {
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
		return artistDao.selectCountByName(name);
	}

	@Override
	public boolean refreshPlayCount(int id, int playCount) {
		Object object = redisUtil.hget("artist_play_count", String.valueOf("id"));
		if(object == null) {
			redisUtil.hset("artist_play_count", String.valueOf("id"), playCount);
			return true;
		}
		redisUtil.hdel("artist_play_count", String.valueOf("id"));
		redisUtil.hset("artist_play_count", String.valueOf("id"), playCount);
		return true;
	}

	@Override
	public int getFilterCount(String initial, int region, int gender) {
		Object object = redisUtil.hget("artist_filter_count", initial+"_"+region+"_"+gender);
		if(object == null) {
			int count = artistDao.selectCountByCategory(initial, region, gender);
			redisUtil.hset("artist_filter_count", initial+"_"+region+"_"+gender, count,TimeConstant.A_DAY);
			return count;
		}
		return (int) object;
	}

}
