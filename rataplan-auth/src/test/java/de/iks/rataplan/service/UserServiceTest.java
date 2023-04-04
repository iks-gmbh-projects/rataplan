package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.USER_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.iks.rataplan.domain.UserDTO;
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
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.MailAlreadyInUseException;
import de.iks.rataplan.exceptions.UsernameAlreadyInUseException;
import de.iks.rataplan.exceptions.WrongCredentialsException;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class UserServiceTest {

	private static final String BASE_LINK = "classpath:test/db/service/user";
	private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
	private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;

	@Autowired
	private UserService userService;


	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUser() {
		User user = new User(2, "fritz@fri.tte", " fritz  ", "password", " fritz");
		UserDTO userDTO = new UserDTO(user);
		userDTO.setPassword(user.getPassword());
		User registeredUser= userService.getUserFromId(userService.registerUser(userDTO).getId());
		assertEquals(registeredUser.getPassword().length(), 60);
		assertNotNull(registeredUser.getId());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerTrimmedUser() {
		User user = new User(2, "fritz@fri.tte", " fritz  ", "password", " fritz");
		UserDTO userDTO= new UserDTO(user);
		userDTO.setPassword(user.getPassword());
		User registeredUser = userService.getUserFromId( userService.registerUser(userDTO).getId());
		assertEquals(registeredUser.getPassword().length(), 60);
		assertNotNull(registeredUser.getId());
	}

	@Test(expected = UsernameAlreadyInUseException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailUsernameAlreadyExists() {
		userService.registerUser(new UserDTO(1,"PeTEr","peter","neuerpeter@sch.mitz","password"));

	}

	@Test(expected = MailAlreadyInUseException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailMailAlreadyExists() {
		userService.registerUser(new UserDTO(1,"neuerpeter","peter","PEtEr@scH.MiTz","password"));
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsername() {

		UserDTO dbUser = userService.loginUser(new UserDTO(1,"PEtEr",null,null,"geheim"));
		User user = userService.getUserFromId(dbUser.getId());
		assertEquals("peter", dbUser.getUsername());
		assertEquals("peter@sch.mitz", dbUser.getMail());

		assertEquals(60, user.getPassword().length());
	}

	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsernameShouldFailUsernameDoesNotExist() {

		userService.loginUser(new UserDTO(1,"DoesNotExist",null,null,"geheim"));
	}

	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsernameShouldFailWrongPassword() {
		 userService.loginUser(new UserDTO(1,"PEtEr",null,null,"wrongPassword"));
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithMail() {

		UserDTO dbUser = userService.loginUser(new UserDTO(1,null,null,"peter@sch.mitz","geheim"));
		User user = userService.getUserFromId(dbUser.getId());
		assertEquals("peter", dbUser.getUsername());
		assertEquals("peter@sch.mitz", dbUser.getMail());

		assertEquals(60, user.getPassword().length());
	}

	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithMailShouldFailWrongPassword() {
		userService.loginUser(new UserDTO(1,null,null,"peter@sch.mitz","wrongPassword"));

	}

	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithMailShouldFailMailDoesNotExist() {
		userService.loginUser(new UserDTO(1,null,null,"does@not.exist","wrongPassword"));
	}

}
