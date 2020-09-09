package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.BACKENDUSERS;
import static de.iks.rataplan.testutils.TestConstants.CREATE;
import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.GET;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static de.iks.rataplan.testutils.TestConstants.SERVICE;
import static de.iks.rataplan.testutils.TestConstants.UPDATE;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
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
import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.domain.BackendUserAccess;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class BackendUserServiceTest {

	private static final String FILE_PATH = PATH + SERVICE + BACKENDUSERS;

	@Autowired
	private BackendUserService backendUserService;

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createBackendUser() throws Exception {
		backendUserService.createBackendUser(new BackendUser(2));
	}

	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createBackendUserShouldFailAuthUserIdAlreadyExists() throws Exception {
		// throws Exception, test ends here
		backendUserService.createBackendUser(new BackendUser(1));
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + GET + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getBackendUser() throws Exception {
		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(1);

		assertEquals(Integer.valueOf(1), backendUser.getId());
		assertEquals(Integer.valueOf(1), backendUser.getAuthUserId());
		assertEquals(0, backendUser.getUserAccess().size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + GET + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getBackendUserShouldFailAuthUserIdDoesNotExist() throws Exception {
		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(99);

		assertEquals(null, backendUser);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateBackendUser() throws Exception {
		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(2);

		List<BackendUserAccess> userAccess = new ArrayList<>();

		userAccess.add(new BackendUserAccess(1, backendUser.getId(), true, false));
		userAccess.add(new BackendUserAccess(2, backendUser.getId(), false, true));

		backendUser.setUserAccess(userAccess);

		backendUserService.updateBackendUser(backendUser);
	}

	/*
	 * TODO
	 * 
	 * Achtung: Was soll der Test machen? - Vor- und Nachname abändern - BackendUserAccess Datensätze hinzufügen: einer
	 * ist valide, einer invalide
	 * 
	 * Was macht der Test? - den ersten BackendUserAccess Datensatz abändern, da er valide ist - eine
	 * DataIntegrityViolationException beim hinzufügen des zweiten Datensatzes werfen, da er invalide ist --> bei
	 * umgekehrter Reihenfolge würde die Exception im ersten Datensatz fliegen und somit keine Änderungen in der
	 * Datenbank vorgenommen werden - Vor- und Nachname NICHT abändern
	 * 
	 */
	@Ignore
	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateBackendUserShouldFailRequestIdDoesNotExist() throws Exception {
		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(2);

		List<BackendUserAccess> userAccess = new ArrayList<>();

		userAccess.add(new BackendUserAccess(1, backendUser.getId(), true, false));
		userAccess.add(new BackendUserAccess(99, backendUser.getId(), false, true));

		backendUser.setUserAccess(userAccess);

		backendUserService.updateBackendUser(backendUser);
	}

}
