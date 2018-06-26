package task;

import static org.junit.Assert.assertTrue;

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
//import com.musicweb.task.PlayCountSychronizeTask;
import com.musicweb.util.RedisUtil;

import base.BaseTest;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-dao.xml",
//"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-redis.xml"})
//@Rollback(false)
//@Transactional(transactionManager = "transactionManager")
public class TaskTest extends BaseTest{
//	@Resource
//	private PlayCountSychronizeTask playCountSynchronizeTask;
	
	@Resource
	private AlbumDao albumDao;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private SongDao songDao;
	
	@Resource
	private RedisUtil redisUtil;
	
	@Test
	public void testTask() {
		redisUtil.hset("song_play_count", "1", 2);
		Song song = new Song();
		song.setId(1);
		redisUtil.hset("song", "1", song);
		System.out.println("刷新到数据库前：");
		song = songDao.selectById(1);
		System.out.println(((Song)redisUtil.hget("song", "1")).getPlayCount());
		System.out.println(song.getPlayCount());
		try {
			Thread.sleep(1000*12);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("刷新到数据库后:");
		Song songNew = songDao.selectById(1);
		System.out.println(songNew.getPlayCount());
		System.out.println(((Song)redisUtil.hget("song", "1")).getPlayCount());
	}
}
