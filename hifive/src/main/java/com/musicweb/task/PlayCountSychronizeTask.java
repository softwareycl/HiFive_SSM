package com.musicweb.task;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.SongDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.util.RedisUtil;

@Component
public class PlayCountSychronizeTask {
	@Autowired
	private AlbumDao albumDao;
	@Autowired
	private SongDao songDao;
	@Autowired
	private ArtistDao singerDao;
	@Autowired
	RedisUtil redisUtil;

	/*
	 * 每天凌晨01:00更新数据库一次
	 */
	@Scheduled(cron = "0 0 1 * * *")
	public void sychronizePlayCount() {
		Map<Object, Object> songPlayCountMap = redisUtil.hmget("songPlayCount");
		for (Map.Entry<Object, Object> entry : songPlayCountMap.entrySet()) {
			songDao.updatePlayCount((Integer) entry.getKey(), (Integer) entry.getValue());
		}

		Map<Object, Object> singerPlayCountMap = redisUtil.hmget("singerPlayCount");
		for (Map.Entry<Object, Object> entry : singerPlayCountMap.entrySet()) {
			singerDao.updatePlayCount((Integer) entry.getKey(), (Integer) entry.getValue());
		}

		Map<Object, Object> albumPlayCountMap = redisUtil.hmget("albumPlayCount");
		for (Map.Entry<Object, Object> entry : albumPlayCountMap.entrySet()) {
			albumDao.updatePlayCount((Integer) entry.getKey(), (Integer) entry.getValue());
		}
	}

}
