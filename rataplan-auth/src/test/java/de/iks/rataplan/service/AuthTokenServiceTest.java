package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthToken;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.repository.AuthTokenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;

import java.nio.charset.StandardCharsets;

import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
    AuthTokenServiceImpl.class,
    UserServiceImpl.class,
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    FlywayAutoConfiguration.class
})
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = AuthTokenRepository.class)
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class AuthTokenServiceTest {
    
    private static final String BASE_LINK = "classpath:test/db/service/authToken";
    private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    
    @MockBean
    private CryptoServiceImpl cryptoService;
    @MockBean
    private BCryptPasswordEncoder pwe;
    @MockBean
    private SurveyToolMessageService stms;
    @MockBean
    private BackendMessageService bms;
    @MockBean
    private JwtTokenService jts;
    
    @Autowired
    private AuthTokenService authTokenService;
    
    @BeforeEach
    void setup() {
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .then(a -> a.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDBRaw(any(byte[].class)))
            .then(a -> new String(a.getArgument(0), StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDB(any())).thenCallRealMethod();
        lenient().when(cryptoService.encryptDB(anyString())).thenCallRealMethod();
    }
    
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
