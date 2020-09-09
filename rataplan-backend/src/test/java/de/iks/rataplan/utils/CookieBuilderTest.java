package de.iks.rataplan.utils;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.utils.CookieBuilder;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class CookieBuilderTest {
	
	@Autowired
	private CookieBuilder cookieBuilder;
	
	@Test
	public void createJWTCookie() {
		String value = "this is a jwt token";
		
		Cookie cookie = cookieBuilder.createJWTCookie(value, false);
		
		assertEquals("jwttoken", cookie.getName());
		assertEquals(value, cookie.getValue());
		assertEquals(60000, cookie.getMaxAge());
		assertEquals("localhost", cookie.getDomain());
		assertEquals("/", cookie.getPath());
		assertEquals(true, cookie.isHttpOnly());
	}
	
	@Test
	public void logoutJWTCookie() {
		String value = "this is a jwt token";
		
		Cookie cookie = cookieBuilder.createJWTCookie(value, true);
		
		assertEquals("jwttoken", cookie.getName());
		assertEquals(value, cookie.getValue());
		assertEquals(0, cookie.getMaxAge());
		assertEquals("localhost", cookie.getDomain());
		assertEquals("/", cookie.getPath());
		assertEquals(true, cookie.isHttpOnly());
	}
	
}
