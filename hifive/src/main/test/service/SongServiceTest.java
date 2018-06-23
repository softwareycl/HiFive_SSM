package service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.musicweb.domain.Song;
import com.musicweb.service.SongService;

/**
 * 
 * @author brian
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml",
"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-redis.xml"})
public class SongServiceTest {
	
	@Resource
	private SongService songService;
	
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
	
	//
	@Test
	public void testGetDuration() {
		
	}

}
