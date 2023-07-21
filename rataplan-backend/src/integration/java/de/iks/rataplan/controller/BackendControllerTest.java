package de.iks.rataplan.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.IntegrationConfig;
import de.iks.rataplan.controller.BackendControllerTest.MockHttpServletRequestConfig;
import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;
import de.iks.rataplan.restservice.AuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, IntegrationConfig.class, MockHttpServletRequestConfig.class})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class BackendControllerTest {
    @TestConfiguration
    public static class MockHttpServletRequestConfig {
        @Bean
        @Primary
        public HttpServletResponse httpServletResponse() {
            return new MockHttpServletResponse();
        }
    }
    private static final String FILE_PATH = PATH + CONTROLLER + BACKEND;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private BackendController backendController;
    
    @Autowired
    private VoteParticipantRepository rawRepository1;
    
    @Autowired
    private VoteRepository rawRepository2;

    @Before
    public void mockSecret() {
        when(authService.isValidIDToken(anyString()))
            .thenAnswer(invocation -> "validToken".equals(invocation.getArgument(0, String.class)));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDelete() {
        assertEquals(HttpStatus.OK, backendController.deleteData(2, "validToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDeleteBadToken() {
        assertEquals(HttpStatus.FORBIDDEN, backendController.deleteData(2, "badToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }

    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testAnonymize() {
        assertEquals(HttpStatus.OK, backendController.anonymizeData(2, "validToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testAnonymizeBadToken() {
        assertEquals(HttpStatus.FORBIDDEN, backendController.anonymizeData(2, "badToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
}
