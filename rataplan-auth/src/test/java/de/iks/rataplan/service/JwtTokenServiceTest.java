package de.iks.rataplan.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.User;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class JwtTokenServiceTest {

	@Autowired
	private JwtTokenService jwtTokenService;

	@Test
	public void generateTokenAndValidateTokenAndGetUsernameFromToken() {
		User user = new User();

		user.setUsername("Peter");
		user.setMail("peter@sch.mitz");
		user.setPassword("geheim");

		String token = jwtTokenService.generateToken(user);
		assertNotNull(token);

		assertTrue(jwtTokenService.isTokenValid(token));

		String username = jwtTokenService.getUsernameFromToken(token);
		assertEquals(username, "Peter");
	}

}
