package de.iks.rataplan.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.repository.BackendUserRepository;
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

import static de.iks.rataplan.testutils.TestConstants.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class BackendControllerTest {
    private static final String FILE_PATH = PATH + CONTROLLER + BACKEND;
    
    @Autowired
    private BackendController backendController;
    
    @Autowired
    private BackendUserRepository rawRepository;
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDelete() {
        backendController.deleteData(2, null);
        rawRepository.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testAnonymize() {
        backendController.anonymizeData(2, null);
        rawRepository.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
}
