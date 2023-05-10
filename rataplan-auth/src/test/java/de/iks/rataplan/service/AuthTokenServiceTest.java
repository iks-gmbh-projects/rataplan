package de.iks.rataplan.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.AuthToken;
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

import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AuthTokenServiceTest {

    private static final String BASE_LINK = "classpath:test/db/service/authToken";
    private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;

    @Autowired
    private AuthTokenService authTokenService;

    @Test
    public void generateAuthToken() {
        String token = authTokenService.generateAuthToken(6);
        assertNotNull(token);
    }

    @Test
    public void checkValidForm() {
        String token = authTokenService.generateAuthToken(64);
        assertTrue(String.format("Invalid Token: %s", token), token.matches("[a-zA-Z0-9]{64}"));
    }

    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void setAuthTokenInDB() {
        AuthToken authToken = authTokenService.saveAuthTokenToUserWithMail("peter@sch.mitz");
        assertNotNull(authToken.getToken());
        assertEquals(6, authToken.getToken().length());
    }
    
    @Test(expected = Exception.class)
    @DatabaseSetup(USER_FILE_INITIAL)
    public void setAuthTokenInDBViaBadEmail() {
        authTokenService.saveAuthTokenToUserWithMail("KoBbpLwGdBuAgJDBBIYmfQ==");
    }
}
