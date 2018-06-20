package com.musicweb.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.musicweb.constant.TimeConstant;
import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.service.PlaylistService;
import com.musicweb.util.RedisUtil;
import com.musicweb.service.CacheService;

@Service("playlistService")
public class PlaylistServiceImpl implements PlaylistService {

	@Resource
	private PlaylistDao playlistDao;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private SongDao songDao;
	@Resource
	private CacheService cacheService;
	@Resource
	private RedisUtil redisUtil;

	private static final String PLAYLIST = "playlist";
	private static final String PLAYLIST_SONGS = "playlist_songs";
	@Override
	public Playlist getInfo(int id) {
		Playlist playlist = cacheService.getAndCachePlaylistByPlaylistID(id);
		return playlist;
	}

	@Override
	public int create(Playlist playlist) {
		int id = playlistDao.insert(playlist);
		redisUtil.hset(PLAYLIST, String.valueOf(playlist.getId()), playlist, TimeConstant.A_DAY);
		return id;
	}

	@Override
	public boolean setImage(int id, String image) {
		playlistDao.updateImage(id, image);
		return true;
	}

	@Override
	public boolean modifyInfo(Playlist playlist) {
		playlistDao.update(playlist);
		redisUtil.hdel(PLAYLIST, String.valueOf(playlist.getId()));
		redisUtil.hset(PLAYLIST, String.valueOf(playlist.getId()), playlist, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean addSong(int playlistId, int songId) {
		playlistDao.insertSong(playlistId, songId);
		List<Song> songList = playlistDao.selectAllSongs(playlistId);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(playlistId));
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean removeSong(int playlistId, int songId) {
		playlistDao.deleteSong(playlistId, songId);
		List<Song> songList = playlistDao.selectAllSongs(playlistId);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(playlistId));
		if(songList != null)
			redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean addPlaylistToPlaylist(int fromId, int toId) {
		playlistDao.insertPlaylistToPlaylist(fromId, toId);
		List<Song> songList = playlistDao.selectAllSongs(toId);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(toId));
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(toId), songList, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean addAlbumToPlaylist(int albumId, int playlistId) {
		playlistDao.insertAlbumToPlaylist(albumId, playlistId);
		List<Song> songList = playlistDao.selectAllSongs(playlistId);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(playlistId));
		redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
		return true;
	}

	@Override
	public boolean remove(int id) {
		playlistDao.delete(id);
		redisUtil.hdel(PLAYLIST_SONGS, String.valueOf(id));
		redisUtil.hdel(PLAYLIST, String.valueOf(id));
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Song> getSongList(int playlistId) {
		Object object = redisUtil.hget(PLAYLIST_SONGS, String.valueOf(playlistId));
		if(object == null) {
			List<Song> songList = playlistDao.selectAllSongs(playlistId);
			if(songList != null) {
				redisUtil.hset(PLAYLIST_SONGS, String.valueOf(playlistId), songList, TimeConstant.A_DAY);
			}
			return songList;
		}
		return (List<Song>) object;
	}

}
