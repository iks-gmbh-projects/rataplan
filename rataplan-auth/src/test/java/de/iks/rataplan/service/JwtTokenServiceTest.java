package de.iks.rataplan.service;

import de.iks.rataplan.dto.UserDTO;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
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
