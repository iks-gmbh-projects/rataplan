package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthToken;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
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
        assertTrue(token.matches("[a-zA-Z0-9]{64}"), String.format("Invalid Token: %s", token));
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void setAuthTokenInDB() {
        AuthToken authToken = authTokenService.saveAuthTokenToUserWithMail("peter@sch.mitz");
        assertNotNull(authToken.getToken());
        assertEquals(6, authToken.getToken().length());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void setAuthTokenInDBViaBadEmail() {
        assertThrows(Exception.class, () -> authTokenService.saveAuthTokenToUserWithMail("KoBbpLwGdBuAgJDBBIYmfQ=="));
    }
}
