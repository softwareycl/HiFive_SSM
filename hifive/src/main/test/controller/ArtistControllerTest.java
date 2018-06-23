package controller;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.musicweb.controller.ArtistController;
import com.musicweb.domain.Artist;
import com.musicweb.view.SimpleArtistView;

import base.BaseTest;

public class ArtistControllerTest extends BaseTest {
	@Autowired
	   protected ArtistController artistController;
	
	@Test
	public void testFilterArtistByCategory() {
		int n = artistController.filterCount("@", 1, 0);
//		for(SimpleArtistView artistView : list)
			System.out.println(n);
	}

}
