package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.Assert.*;

import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.*;
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

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class UserServiceTest {

	private static final String BASE_LINK = "classpath:test/db/service/user";
	private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
	private static final String ENCRYPTED_USER_FILE_INITIAL = BASE_LINK + "/encrypted" + FILE_INITIAL;
	private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;

	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenService jwtTokenService;


	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUser() {
		UserDTO userDTO = new UserDTO(2, "fritz", " fritz", "fritz@fri.tte", "password");
		User registeredUser= userService.getUserFromId(userService.registerUser(userDTO).getId());
		assertEquals(registeredUser.getPassword().length(), 60);
		assertNotNull(registeredUser.getId());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerTrimmedUser() {
		UserDTO userDTO= new UserDTO(2, " fritz ", " fritz", "fritz@fri.tte", "password");
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
	
	@Test(expected = InvalidUserDataException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailUsernameOnlyWhitespace() {
		userService.registerUser(new UserDTO(1,"  ","peter","neuerpeter@sch.mitz","password"));
	}
	
	@Test(expected = InvalidUserDataException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailEmailOnlyWhitespace() {
		userService.registerUser(new UserDTO(1,"neuerpeter","peter","  ","password"));
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsername() {
		UserDTO dbUser = userService.loginUser(new UserDTO(1,"PEtEr",null,null,"geheim"));
		assertEquals("peter", dbUser.getUsername());
		assertEquals("peter@sch.mitz", dbUser.getMail());
	}

	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsernameShouldFailUsernameDoesNotExist() {
		userService.loginUser(new UserDTO(1,"DoesNotExist",null,null,"geheim"));
	}
	
	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
	@ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsernameShouldFailUsernameDoesNotExist2() {
		userService.loginUser(new UserDTO(1,"/L81z0oXEO3vgkU25CCiIw==",null,null,"geheim"));
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
		assertEquals("peter", dbUser.getUsername());
		assertEquals("peter@sch.mitz", dbUser.getMail());
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
	
	@Test(expected = WrongCredentialsException.class)
	@DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
	@ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserWithUsernameShouldFailMailDoesNotExist2() {
		userService.loginUser(new UserDTO(1,"KoBbpLwGdBuAgJDBBIYmfQ==",null,null,"geheim"));
	}
	
	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	public void updateUserProfileTest() {
		assertTrue(userService.updateProfileDetails(new UserDTO(1, "peter", "GeänderterPeter", "peter@sch.mitz", null)));
		
		UserDTO changed = userService.getUserDTOFromUsername("peter");
		assertNotNull(changed);
		assertEquals((Integer)1, changed.getId()); //verify that we got the correct user
		assertEquals("GeänderterPeter", changed.getDisplayname());
		assertEquals("peter@sch.mitz", changed.getMail());
	}

	@Test(expected = UnconfirmedAccountException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	public void loginShouldFailIfAccountNotConfirmed() {
		userService.loginUser(new UserDTO(3,"john",null,null,"geheim"));
	}

	@Test
	@DatabaseSetup(value = USER_FILE_INITIAL)
	public void blockConfirmationEmailResendIfAccountDoesntExist(){
		assertNull(userService.validateResendConfirmationEmailRequest("abc"));
	}

	@Test
	@DatabaseSetup(value = USER_FILE_INITIAL)
	public void blockConfirmationEmailResendIfAccountAlreadyConfirmed(){
		assertNull(userService.validateResendConfirmationEmailRequest("peter@sch.mitz"));
	}

	@Test
	@DatabaseSetup(value = USER_FILE_INITIAL)
	public void confirmAccount(){
		UserDTO user = userService.registerUser(new UserDTO(2, "fritz", " fritz", "fritz@fri.tte", "password"));
		assertFalse(userService.getUserFromUsername(user.getUsername()).isAccountConfirmed());

		String token = jwtTokenService.generateAccountConfirmationToken(user);
		userService.confirmAccount(token);

		assertTrue(userService.getUserFromUsername("fritz").isAccountConfirmed());
	}
}
