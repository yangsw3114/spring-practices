package com.douzone.container.config.videosystem.mixing;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.douzone.container.videosystem.DVDPlayer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DVDPlayerConfig.class)
public class DVDPlayerMixingConfig01Test {
	@Autowired
	private DVDPlayer dvdPlayer;
	
	@Test
	public void testPlayerNotNull() {
		assertNotNull(dvdPlayer);
	}
}