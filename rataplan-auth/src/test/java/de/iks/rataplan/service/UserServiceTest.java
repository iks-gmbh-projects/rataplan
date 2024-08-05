package de.iks.rataplan.service;

import de.iks.rataplan.config.FrontendConfig;
import de.iks.rataplan.config.IDKeyConfig;
import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.*;
import de.iks.rataplan.repository.UserRepository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.TestExecutionListeners;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
    classes = {
        UserServiceImpl.class,
        JwtTokenServiceImpl.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class,
        JwtConfig.class,
        IDKeyConfig.class,
        BCryptPasswordEncoder.class,
        LogMailServiceImpl.class,
        FrontendConfig.class
    }
)
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class UserServiceTest {
    
    private static final String BASE_LINK = "classpath:test/db/service/user";
    private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    private static final String ENCRYPTED_USER_FILE_INITIAL = BASE_LINK + "/encrypted" + FILE_INITIAL;
    private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;
    
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenService jwtTokenService;
    @MockBean
    private CryptoService cryptoService;
    @MockBean
    private SurveyToolMessageService stms;
    @MockBean
    private BackendMessageService bms;
    @Autowired
    @Qualifier("jwkSet")
    private JWKSet jwkSet;
    private JwtDecoder jwtDecoder;
    
    @BeforeEach
    public void setup() {
        when(cryptoService.encryptDBRaw(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class)
            .getBytes(StandardCharsets.UTF_8));
        when(cryptoService.encryptDB(anyString())).thenCallRealMethod();
        when(cryptoService.decryptDBRaw(any())).thenAnswer(invocation -> new String(invocation.getArgument(0,
            byte[].class
        ), StandardCharsets.UTF_8));
        when(cryptoService.decryptDB(any())).thenCallRealMethod();
        
        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(jwkSet.getKeys()
            .stream()
            .map(JWK::getAlgorithm)
            .filter(Objects::nonNull)
            .map(Algorithm::toString)
            .map(JWSAlgorithm::parse)
            .collect(Collectors.toUnmodifiableSet()), new ImmutableJWKSet<>(jwkSet)));
        jwtDecoder = new NimbusJwtDecoder(processor);
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUser() {
        UserDTO userDTO = new UserDTO(4, "fritz", " fritz", "fritz@fri.tte", "password");
        User registeredUser = userService.getUserFromId(userService.registerUser(userDTO).getId());
        assertEquals(registeredUser.getPassword().length(), 60);
        assertNotNull(registeredUser.getId());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerTrimmedUser() {
        UserDTO userDTO = new UserDTO(4, " fritz ", " fritz", "fritz@fri.tte", "password");
        User registeredUser = userService.getUserFromId(userService.registerUser(userDTO).getId());
        assertEquals(registeredUser.getPassword().length(), 60);
        assertNotNull(registeredUser.getId());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailUsernameAlreadyExists() {
        assertThrows(UsernameAlreadyInUseException.class,
            () -> userService.registerUser(new UserDTO(4, "PeTEr", "peter", "neuerpeter@sch.mitz", "password"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailMailAlreadyExists() {
        assertThrows(MailAlreadyInUseException.class,
            () -> userService.registerUser(new UserDTO(4, "neuerpeter", "peter", "PEtEr@scH.MiTz", "password"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailUsernameOnlyWhitespace() {
        assertThrows(InvalidUserDataException.class,
            () -> userService.registerUser(new UserDTO(4, "  ", "peter", "neuerpeter@sch.mitz", "password"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailEmailOnlyWhitespace() {
        assertThrows(InvalidUserDataException.class,
            () -> userService.registerUser(new UserDTO(4, "neuerpeter", "peter", "  ", "password"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithUsername() {
        UserDTO dbUser = userService.loginUser(new UserDTO(1, "PEtEr", null, null, "geheim"));
        assertEquals("peter", dbUser.getUsername());
        assertEquals("peter@sch.mitz", dbUser.getMail());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithUsernameShouldFailUsernameDoesNotExist() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, "DoesNotExist", null, null, "geheim"))
        );
    }
    
    @Test
    @DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
    @ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithUsernameShouldFailUsernameDoesNotExist2() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, "/L81z0oXEO3vgkU25CCiIw==", null, null, "geheim"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithUsernameShouldFailWrongPassword() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, "PEtEr", null, null, "wrongPassword"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithMail() {
        
        UserDTO dbUser = userService.loginUser(new UserDTO(1, null, null, "peter@sch.mitz", "geheim"));
        assertEquals("peter", dbUser.getUsername());
        assertEquals("peter@sch.mitz", dbUser.getMail());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithMailShouldFailWrongPassword() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, null, null, "peter@sch.mitz", "wrongPassword"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithMailShouldFailMailDoesNotExist() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, null, null, "does@not.exist", "wrongPassword"))
        );
    }
    
    @Test
    @DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
    @ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void loginUserWithUsernameShouldFailMailDoesNotExist2() {
        assertThrows(WrongCredentialsException.class,
            () -> userService.loginUser(new UserDTO(1, "KoBbpLwGdBuAgJDBBIYmfQ==", null, null, "geheim"))
        );
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void updateUserProfileTest() throws CredentialNotFoundException {
        assertTrue(userService.updateProfileDetails(new UserDTO(1,
            "peter",
            "GeänderterPeter",
            "peter@sch.mitz",
            "geheim"
        )));
        
        UserDTO changed = userService.getUserDTOFromUsername("peter");
        assertNotNull(changed);
        assertEquals((Integer) (-1), changed.getId()); //verify that we got the correct user
        assertEquals("GeänderterPeter", changed.getDisplayname());
        assertEquals("peter@sch.mitz", changed.getMail());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void loginShouldFailIfAccountNotConfirmed() {
        assertThrows(UnconfirmedAccountException.class,
            () -> userService.loginUser(new UserDTO(3, "john", null, null, "geheim"))
        );
    }
    
    @Test
    @DatabaseSetup(value = USER_FILE_INITIAL)
    public void blockConfirmationEmailResendIfAccountDoesntExist() {
        assertNull(userService.validateResendConfirmationEmailRequest("abc"));
    }
    
    @Test
    @DatabaseSetup(value = USER_FILE_INITIAL)
    public void blockConfirmationEmailResendIfAccountAlreadyConfirmed() {
        assertNull(userService.validateResendConfirmationEmailRequest("peter@sch.mitz"));
    }
    
    @Test
    @DatabaseSetup(value = USER_FILE_INITIAL)
    public void confirmAccount() {
        UserDTO user = userService.registerUser(new UserDTO(4, "fritz", " fritz", "fritz@fri.tte", "password"));
        assertFalse(userService.getUserFromUsername(user.getUsername()).isAccountConfirmed());
        
        String token = jwtTokenService.generateAccountConfirmationToken(user);
        assertNotNull(token);
        Jwt jwt = jwtDecoder.decode(token);
        assertEquals(JwtTokenService.SCOPE_ACCOUNT_CONFIRMATION, jwt.getClaimAsString(JwtTokenService.CLAIM_SCOPE));
        userService.confirmAccount(jwt);
        
        assertTrue(userService.getUserFromUsername("fritz").isAccountConfirmed());
    }
    
    @Test
    @DatabaseSetup(value = USER_FILE_INITIAL)
    public void getEmailFromId() {
        assertEquals("peter@sch.mitz", cryptoService.decryptDB(userService.getUserFromId(-1).getMail()));
    }
}