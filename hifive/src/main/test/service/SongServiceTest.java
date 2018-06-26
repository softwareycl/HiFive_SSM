package service;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.service.SongService;
import com.musicweb.util.RedisUtil;

/**
 * 
 * @author brian
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml",
"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-redis.xml"})
//@Rollback(false)
//@Transactional(transactionManager = "transactionManager")
public class SongServiceTest {
	
	@Resource
	private SongService songService;
	
	@Resource
	private AlbumDao albumDao;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private SongDao songDao;
	
	@Resource
	private RedisUtil redisUtil;
	
	/*
	 * 以下测试不涉及DML操作，不回滚
	 */
	
	//测试成功
	@Test
	public void testSearch() {
		List<Song> songs = songService.search("我", 1);
		if(songs == null) {
			System.out.println("没有数据");
			return;
		}
		System.out.println(songs.size());
		for(Song song: songs) {
			System.out.println(song);
		}
	}
	
	//测试成功
	@Test
	public void testGetSearchCount() {
		System.out.println(songService.getSearchCount("我"));
	}
	
	//成功运行，但由于没有播放量无法验证结果是否正确
	@Test
	public void testLookUpRank() {
		List<Song> songs = songService.lookUpRank(2, true);
		if(songs == null) {
			System.out.println("没有数据");
			return;
		}
		System.out.println(songs.size());
		for(Song song: songs) {
			System.out.println(song);
		}
	}
	
	//测试成功
	@Test
	public void testGetInfo() {
		Song song = songService.getInfo(1);
		System.out.println(song);
	}
	
	//基本测试成功了
	@Test
	public void testLookUpNewSongs() {
		List<Song> songs = songService.lookUpNewSongs(1);
		if(songs == null) {
			System.out.println("没有数据");
			return;
		}
		System.out.println(songs.size());
		for(Song song: songs) {
			System.out.println(song.getName() + ":\t" + song.getRegion() + "\t" + song.getDuration());
		}
	}
	
	//测试成功
	@Test
	public void testGetDuration() {
		String duration = songService.getDuration(1);
		System.out.println(duration);
	}
	
	//测试成功。如果redis与事务同时使用导致set的结果不能马上被get到，因为事务还没提交，set不生效
	@Test
	public void testPlay() {
		int songId = 122;
		System.out.println("播放前，缓存中的歌曲播放量为：" + redisUtil.hget("song_play_count", String.valueOf(songId)));
		Song song = songDao.selectById(songId);
		System.out.println("播放前，缓存中的歌曲所属专辑播放量为：" + redisUtil.hget("album_play_count", String.valueOf(song.getAlbumId())));
		System.out.println("播放前，缓存中的歌曲所属歌手播放量为：" + redisUtil.hget("artist_play_count", String.valueOf(song.getArtistId())));
		
		songService.play(songId);
		
		System.out.println("播放后，缓存中的歌曲播放量为：" + redisUtil.hget("song_play_count", String.valueOf(songId)));
		System.out.println("播放后，缓存中的歌曲所属专辑播放量为：" + redisUtil.hget("album_play_count", String.valueOf(song.getAlbumId())));
		System.out.println("播放后，缓存中的歌曲所属歌手播放量为：" + redisUtil.hget("artist_play_count", String.valueOf(song.getArtistId())));
	}
	
	/*
	 * 以下测试涉及DML操作，均回滚
	 */
	
	//测试成功
	@Test
	@Rollback(false)
	public void testAdd() {
		Song song = new Song();
		song.setAlbumId(1);
		song.setArtistId(1);
		song.setFilePath("/filepath");
		song.setImage("/image");
		song.setLanguage("英语");
		song.setLyricsPath("/lyricspath");
		song.setName("我");
		song.setPlayCount(0);
		song.setRegion(1);
		song.setReleaseDate(new Date());
		song.setStyle(1);
		int i = songService.add(song);
		if(i == -1) {
			System.out.println("插入失败");
			return;
		}
		int id = song.getId();
		System.out.println(id);
		System.out.println(songDao.selectById(id));
	}
	
	//测试成功
	@Test
	@Rollback(true)
	public void testRemove() {
		boolean b = songService.remove(159);
		assertTrue(b);
	}
	
	//测试成功
	@Test
	@Rollback(true)
	public void testModify() {
		System.out.println("modify前：");
		System.out.println(songDao.selectById(159));
		Song song = new Song();
		song.setAlbumId(1);
		song.setArtistId(1);
		song.setId(159);
		song.setName("我");
		song.setLanguage("英语");
		song.setStyle(1);
		song.setReleaseDate(new Date());
		boolean b = songService.modify(song);
		System.out.println("modify后：");
		System.out.println(songDao.selectById(159));
		assertTrue(b);
	}
	
	//测试成功
	@Test
	@Rollback(true)
	public void testSetLyricsPath() {
		System.out.println("设置前：");
		System.out.println(songDao.selectById(159).getLyricsPath());
		String path = "lyrics";
		boolean b = songService.setLyricsPath(159, path);
		System.out.println("设置后：");
		System.out.println(songDao.selectById(159).getLyricsPath());
		assertTrue(b);
	}

	//测试成功
	@Test
	@Rollback(true)
	public void testSetImage() {
		System.out.println("设置前：");
		System.out.println(songDao.selectById(159).getImage());
		String path = "image";
		boolean b = songService.setImage(159, path);
		System.out.println("设置后：");
		System.out.println(songDao.selectById(159).getImage());
		assertTrue(b);
	}
	
	//测试成功
	@Test
	@Rollback(true)
	public void testSetFilePath() {
		System.out.println("设置前：");
		System.out.println(songDao.selectById(159).getFilePath());
		String path = "file";
		boolean b = songService.setFilePath(159, path);
		System.out.println("设置后：");
		System.out.println(songDao.selectById(159).getFilePath());
		assertTrue(b);
	}
	
	@Test
	public void testRedis() {
		System.out.println("---------------------------------123---------------------------------");
		   redisUtil.hset("song_play_count", "332", 11113333);
		   System.out.println(redisUtil.hget("song_play_count", "332"));
	       System.out.println("---------------------------------321---------------------------------");

	}
	
}
