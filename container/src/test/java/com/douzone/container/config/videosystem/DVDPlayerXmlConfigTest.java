package com.douzone.container.config.videosystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.douzone.container.videosystem.DVDPack;
import com.douzone.container.videosystem.DVDPlayer;
import com.douzone.container.videosystem.DigitalVideoDisc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:com/douzone/container/config/videosystem/DVDPlayerConfig.xml"})
public class DVDPlayerXmlConfigTest {
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	// @Autowired
	// 예외발생
	// XML Bean 설정(Avengers)에서는 id를 자동으로 부여 하지 않는다.
	// @Qualifier 사용없음
	private DigitalVideoDisc dvd1;

	@Autowired
	@Qualifier("ironMan")
	private DigitalVideoDisc dvd2;

	@Autowired
	@Qualifier("avengersInfinityWar")
	private DigitalVideoDisc dvd3;
	
	@Autowired
	@Qualifier("avengerEndgame")
	private DigitalVideoDisc dvd4;	

	@Autowired
	@Qualifier("avengerAgeOfUltron")
	private DigitalVideoDisc dvd5;	

	@Autowired
	@Qualifier("avengersCaptainAmerica")
	private DigitalVideoDisc dvd6;	

	@Autowired
	@Qualifier("avengersDirectorEdition")
	private DigitalVideoDisc dvd7;	
	
	@Autowired
	private DVDPack dvdPack;
	
	@Autowired
	@Qualifier("avengersExpansionEdition1")
	private DigitalVideoDisc dvd8;	
	
	@Autowired
	@Qualifier("avengersExpansionEdition2")
	private DigitalVideoDisc dvd9;	

	@Autowired
	@Qualifier("avengersExpansionEdition3")
	private DigitalVideoDisc dvd10;	

	@Autowired
	@Qualifier("dvdPlayer1")
	private DVDPlayer dvdPlayer1;

	@Autowired
	@Qualifier("dvdPlayer2")
	private DVDPlayer dvdPlayer2;

	@Autowired
	@Qualifier("dvdPlayer3")
	private DVDPlayer dvdPlayer3;

	@Autowired
	@Qualifier("dvdPlayer4")
	private DVDPlayer dvdPlayer4;

	@Autowired
	@Qualifier("dvdPlayer5")
	private DVDPlayer dvdPlayer5;
	
	@Ignore
	@Test
	public void testDVD1() {
		assertNotNull(dvd1);
	}
	
	@Test
	public void testDVD2() {
		assertNotNull(dvd2);
	}

	@Test
	public void testDVD3() {
		assertNotNull(dvd3);
	}

	@Test
	public void testDVD4() {
		assertNotNull(dvd4);
	}

	@Test
	public void testDVD5() {
		assertNotNull(dvd5);
	}

	@Test
	public void testDVD6() {
		assertNotNull(dvd6);
	}

	@Test
	public void testDVD7() {
		assertNotNull(dvd7);
	}

	@Test
	public void testDVDPack() {
		// System.out.println(dvdPack);
		assertNotNull(dvdPack);
	}

	@Test
	public void testDVD8() {
		assertNotNull(dvd8);
	}

	@Test
	public void testDVD9() {
		assertNotNull(dvd9);
	}

	@Test
	public void testDVD10() {
		assertNotNull(dvd10);
	}
	
	@Test
	public void testPlay1() {
		dvdPlayer1.play();
		assertEquals("Playing Movie MARVEL's IronMan", systemOutRule.getLog().replace("\r\n", "").replace("\n", ""));
	}

	@Test
	public void testPlay2() {
		dvdPlayer2.play();
		assertEquals("Playing Movie MARVEL's IronMan", systemOutRule.getLog().replace("\r\n", "").replace("\n", ""));
	}

	@Test
	public void testPlay3() {
		dvdPlayer3.play();
		assertEquals("Playing Movie MARVEL's IronMan", systemOutRule.getLog().replace("\r\n", "").replace("\n", ""));
	}

	@Test
	public void testPlay4() {
		dvdPlayer4.play();
		assertEquals("Playing Movie MARVEL's IronMan", systemOutRule.getLog().replace("\r\n", "").replace("\n", ""));
	}

	@Test
	public void testPlay5() {
		dvdPlayer5.play();
		assertEquals("Playing Movie MARVEL's IronMan", systemOutRule.getLog().replace("\r\n", "").replace("\n", ""));
	}
	
}