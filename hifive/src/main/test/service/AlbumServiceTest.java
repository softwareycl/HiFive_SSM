package service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.musicweb.domain.Album;
import com.musicweb.service.AlbumService;
import com.musicweb.util.RedisUtil;

/**
 * 
 * @author brian
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml",
"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-redis.xml"})
public class AlbumServiceTest {
	
	@Resource
	private AlbumService albumService;
	
	@Resource
	private RedisUtil redisUtil;
	
	//测试成功
	@Test
	public void testSearch() {
		List<Album> albums = albumService.search("The", 1);
		System.out.println("查询到的歌曲为：");
		for (Album album : albums) {
			System.out.println(album);
		}
		System.out.println("缓存中的歌曲为：");
		for (Album album : albums) {
			int id = album.getId();
			Object cachedAlbum = redisUtil.hget("album", String.valueOf(id));
			if (cachedAlbum instanceof Album) {
				System.out.println(cachedAlbum);
			}
		}
	}
	
	@Test
	public void testGetInfo() {
		
	}
	
	@Test
	public void testGetSongList() {
		
	}
	
	@Test
	public void testLookUpAlbumsByCatagory() {
		
	}
	
	@Test
	public void testAdd() {
		
	}
	
	@Test
	public void testRemove() {
		
	}
	
	@Test
	public void testModify() {
		
	}
	
	@Test
	public void testSetImage() {
		
	}
	
	@Test
	public void testLookUpNewAlbums() {
		
	}
	
	@Test
	public void testGetSearchCount() {
		
	}
	
	@Test
	public void testGetFilterCount() {
		
	}

}
