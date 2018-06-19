package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Song;

public interface SongService {
	// 搜索
	List<Song> search(String songName, int page);

	// 排行榜
	List<Song> lookUpRank(int type, boolean isAll);

	// 歌曲详情
	Song getInfo(int id);

	// 播放歌曲
	void play(int id);

	int add(Song song);

	boolean remove(int id);

	boolean modify(Song song);

	boolean setImage(int id, String image);	
	
	boolean setLyricsPath(int id, String lyricsPath);
	
	boolean setFilePath(int id, String filePath);

	List<Song> lookUpNewSongs(int region);

	int getSearchCount(String name);
	
	boolean refreshPlayCount(int id, int playCount);
	
	String getDuration(int id);
	
}
