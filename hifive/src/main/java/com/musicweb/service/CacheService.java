package com.musicweb.service;

import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;

/**
 * 缓存模块业务逻辑接口
 * 
 * @author zhanghuakui, likexin, brian
 *
 */
public interface CacheService {
	/**
	 * 从缓存中获取歌手信息，若缓存中不存在，则从数据库获取并放入缓存
	 * @param id 歌手id
	 * @return 歌手信息
	 */
	Artist getAndCacheSingerBySingerID(int singerID);

	/**
	 * 从缓存中获取专辑信息，若缓存中不存在，则从数据库获取并放入缓存
	 * @param id 专辑id
	 * @return 专辑信息
	 */
	Album getAndCacheAlbumByAlbumID(int albumID);

	/**
	 * 从缓存中获取歌曲信息，若缓存中不存在，则从数据库获取并放入缓存
	 * @param id 歌曲id
	 * @return 歌曲信息
	 */
	Song getAndCacheSongBySongID(int songID);

	/**
	 * 从缓存中获取歌单信息，若缓存中不存在，则从数据库获取并放入缓存
	 * @param id 歌单id
	 * @return 歌单信息
	 */
	Playlist getAndCachePlaylistByPlaylistID(int playlistID);
	
	/**
	 * 从缓存中获取用户信息，若缓存中不存在，则从数据库获取并放入缓存
	 * @param id 用户id
	 * @return 用户信息
	 */
	User getAndCacheUserByUserID(String userID);
}