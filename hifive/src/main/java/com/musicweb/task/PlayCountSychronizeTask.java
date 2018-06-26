package com.musicweb.task;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.CacheService;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.util.RedisUtil;

/**
 * 定时任务刷新playcount至数据库
 * @author likexin
 * @Date 2018/6/26
 */
@Component
public class PlayCountSychronizeTask {
	@Autowired
	private AlbumDao albumDao;
	
	@Autowired
	private SongDao songDao;
	
	@Autowired
	private ArtistDao artistDao;
	
	@Autowired
	CacheService cacheService;
	
	@Autowired
	RedisUtil redisUtil;

	/*
	 * 每天隔五分钟将缓存中的playcount更新到数据库一次, 并且刷新缓存里对象的playcount
	 */
	@Scheduled(cron = "0 0/5 * * * *")
	public void sychronizePlayCount() {
		Map<Object, Object> songPlayCountMap = redisUtil.hmget("song_play_count");
		for (Entry<Object, Object> entry : songPlayCountMap.entrySet()) {
			songDao.updatePlayCount(Integer.parseInt((String)entry.getKey()), (Integer)entry.getValue());
			Song song = cacheService.getAndCacheSongBySongID(Integer.parseInt((String)entry.getKey()));
			song.setPlayCount((Integer)entry.getValue());
			redisUtil.hset("song", String.valueOf(song.getId()), song);
		}

		Map<Object, Object> artistPlayCountMap = redisUtil.hmget("artist_play_count");
		for (Map.Entry<Object, Object> entry : artistPlayCountMap.entrySet()) {
			artistDao.updatePlayCount(Integer.parseInt((String)entry.getKey()), (Integer) entry.getValue());
			Artist artist = cacheService.getAndCacheSingerBySingerID(Integer.parseInt((String)entry.getKey()));
			artist.setPlayCount((Integer) entry.getValue());
			redisUtil.hset("artist", String.valueOf(artist.getId()), artist);
		}

		Map<Object, Object> albumPlayCountMap = redisUtil.hmget("album_play_count");
		for (Map.Entry<Object, Object> entry : albumPlayCountMap.entrySet()) {
			albumDao.updatePlayCount(Integer.parseInt((String)entry.getKey()), (Integer) entry.getValue());
			Album album = cacheService.getAndCacheAlbumByAlbumID(Integer.parseInt((String)entry.getKey()));
			album.setPlayCount((Integer) entry.getValue());
			redisUtil.hset("album", String.valueOf(album.getId()), album);
		}
	}
}
