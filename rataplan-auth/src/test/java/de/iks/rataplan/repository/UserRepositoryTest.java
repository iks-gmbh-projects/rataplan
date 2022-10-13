package de.iks.rataplan.repository;

import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.USER_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class UserRepositoryTest {

	private static final String BASE_LINK = "classpath:test/db/repository/user";
	private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
	private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createUser() throws Exception {
		User user = userRepository.saveAndFlush(USER_1);

		assertEquals(user.getUsername(), USER_1.getUsername());
		assertEquals(user.getMail(), USER_1.getMail());
		// of course password is equal, it does not get encoded
		assertEquals(user.getPassword(), USER_1.getPassword());
		assertNotNull(user.getId());
	}

	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	public void createUserShouldFailUsernameAlreadyExists() throws Exception {
		userRepository.saveAndFlush(new User(null, "new@ma.il", "Peter", "geheim", "peter"));
	}

	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(USER_FILE_INITIAL)
	public void createUserShouldFailMailAlreadyExists() throws Exception {
		userRepository.saveAndFlush(new User(null, "peter@sch.mitz", "neuer name", "geheim", "peter"));
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	public void getOneByMail() throws Exception {
		User user = userRepository.findOneByMail("PeTer@SCh.mitz");

		assertEquals(Integer.valueOf(1), user.getId());
		assertEquals("Peter", user.getUsername());
		assertEquals("peter@sch.mitz", user.getMail());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	public void getOneByMailShouldFailMailNotInUse() throws Exception {
		User user = userRepository.findOneByMail("does@not.exist");
		assertEquals(null, user);
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	public void getOneByUsername() throws Exception {
		User user = userRepository.findOneByUsername("PeTER");

		assertEquals(Integer.valueOf(1), user.getId());
		assertEquals("Peter", user.getUsername());
		assertEquals("peter@sch.mitz", user.getMail());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	public void getOneByUsernameShouldFailMailNotInUse() throws Exception {
		User user = userRepository.findOneByUsername("doesnotexist");
		assertEquals(null, user);
	}
}
