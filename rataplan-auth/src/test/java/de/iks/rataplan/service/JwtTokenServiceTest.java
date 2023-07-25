package de.iks.rataplan.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.iks.rataplan.dto.UserDTO;
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
		UserDTO user = new UserDTO();

		user.setUsername("Peter");
		user.setMail("peter@sch.mitz");
//		user.setPassword("geheim");

		String token = jwtTokenService.generateLoginToken(user);
		assertNotNull(token);

		assertTrue(jwtTokenService.isTokenValid(token));

		String username = jwtTokenService.getUsernameFromToken(token);
		assertEquals(username, "Peter");
	}

	@Test
	public void generateAccountConfirmationAndRetrieveId(){
		UserDTO user = new UserDTO();
		user.setId(1);

		String token = jwtTokenService.generateAccountConfirmationToken(user);
		Integer userId = jwtTokenService.getUserIdFromAccountConfirmationToken(token);

		assertNotNull(token);
		assertTrue(jwtTokenService.isTokenValid(token));
		assertEquals(userId,user.getId());
	}

}
