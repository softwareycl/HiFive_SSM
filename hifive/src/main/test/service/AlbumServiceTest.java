package service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.musicweb.dao.AlbumDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Song;
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
	private AlbumDao albumDao;
	
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
	
	//测试成功
	@Test
	public void testGetInfo() {
		Album album = albumService.getInfo(1);
		System.out.println("查询到的歌曲为：");
		System.out.println(album);
		System.out.println("缓存中的歌曲为：");
		System.out.println(redisUtil.hget("album", String.valueOf(1)));
	}
	
	//测试成功
	@Test
	public void testGetSongList() {
		System.out.println("调用前：");
		System.out.println("缓存中专辑1为：");
		System.out.println(redisUtil.hget("album", String.valueOf(1)));
		System.out.println("缓存中专辑1歌曲为：");
		@SuppressWarnings("unchecked")
		List<Song> songs = (List<Song>)redisUtil.hget("album_songs", String.valueOf(1));
		if (songs != null) {
			for (Song song : songs) {
				System.out.println(song.getAlbumId() + "\t" + song);
			}
		}
		else 
			System.out.println("null");
		//单首歌曲无法测试
		
		albumService.getSongList(1);
		
		System.out.println("调用后：");
		System.out.println("缓存中专辑1为：");
		System.out.println(redisUtil.hget("album", String.valueOf(1)));
		System.out.println("缓存中专辑1歌曲为：");
		@SuppressWarnings("unchecked")
		List<Song> songs1 = (List<Song>)redisUtil.hget("album_songs", String.valueOf(1));
		if (songs1 != null) {
			for (Song song : songs1) {
				System.out.println(song.getAlbumId() + "\t" + song);
			}
		}
		else 
			System.out.println("null");
	}
	
	//测试成功
	@Test
	public void testLookUpAlbumsByCatagory() {
		int region = 0;
		int style = 0;
		int page = 2;
		System.out.println("调用前：");
		System.out.println("缓存中的专辑列表为：");
		@SuppressWarnings("unchecked")
		List<Album> albums = (List<Album>) redisUtil.hget("album_filter", region+"_"+style+"_"+page);
		if (albums != null) {
			for (Album album : albums) {
				System.out.println(album.getRegion() + "\t" + album.getStyle() + "\t" + album);
			}
		}
		else
			System.out.println("null");
		
		albumService.lookUpAlbumsByCatagory(region, style, page);
		
		System.out.println("调用后：");
		System.out.println("缓存中的专辑列表为：");
		@SuppressWarnings("unchecked")
		List<Album> albums1 = (List<Album>) redisUtil.hget("album_filter", region+"_"+style+"_"+page);
		if (albums1 != null) {
			for (Album album : albums1) {
				System.out.println(album.getRegion() + "\t" + album.getStyle() + "\t" + album);
			}
		}
		else
			System.out.println("null");
	}
	
	@Test
	public void testAdd() {
		Album album = new Album();
		album.setArtistId(1);
		album.setImage("/image");
		album.setIntro("intro");
		album.setName("我");
		album.setRegion(1);
		album.setReleaseDate(new Date());
		album.setStyle(1);
		
		int id = albumService.add(album);
		
		if (id != 0) {
			Album album2 = albumDao.select(id);
			System.out.println(album2);
		}
		else
			System.out.println("id没有获取");
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
