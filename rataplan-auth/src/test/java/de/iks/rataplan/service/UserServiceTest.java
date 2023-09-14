package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.*;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
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
    
    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = kpg.generateKeyPair();
        when(cryptoService.encryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(cryptoService.decryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(cryptoService.ensureEncrypted(isA(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
        when(cryptoService.idKeyP()).thenReturn(keyPair.getPrivate());
        when(cryptoService.idKey()).thenReturn(keyPair.getPublic());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerUser() {
        UserDTO userDTO = new UserDTO(2, "fritz", " fritz", "fritz@fri.tte", "password");
        User registeredUser = userService.getUserFromId(userService.registerUser(userDTO).getId());
        assertEquals(registeredUser.getPassword().length(), 60);
        assertNotNull(registeredUser.getId());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerTrimmedUser() {
        UserDTO userDTO = new UserDTO(2, " fritz ", " fritz", "fritz@fri.tte", "password");
        User registeredUser = userService.getUserFromId(userService.registerUser(userDTO).getId());
        assertEquals(registeredUser.getPassword().length(), 60);
        assertNotNull(registeredUser.getId());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerUserShouldFailUsernameAlreadyExists() {
        assertThrows(UsernameAlreadyInUseException.class, () -> {
            userService.registerUser(new UserDTO(1, "PeTEr", "peter", "neuerpeter@sch.mitz", "password"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerUserShouldFailMailAlreadyExists() {
        assertThrows(MailAlreadyInUseException.class, () -> {
            userService.registerUser(new UserDTO(1, "neuerpeter", "peter", "PEtEr@scH.MiTz", "password"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerUserShouldFailUsernameOnlyWhitespace() {
        assertThrows(InvalidUserDataException.class, () -> {
            userService.registerUser(new UserDTO(1, "  ", "peter", "neuerpeter@sch.mitz", "password"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void registerUserShouldFailEmailOnlyWhitespace() {
        assertThrows(InvalidUserDataException.class, () -> {
            userService.registerUser(new UserDTO(1, "neuerpeter", "peter", "  ", "password"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithUsername() {
        UserDTO dbUser = userService.loginUser(new UserDTO(1, "PEtEr", null, null, "geheim"));
        assertEquals("peter", dbUser.getUsername());
        assertEquals("peter@sch.mitz", dbUser.getMail());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithUsernameShouldFailUsernameDoesNotExist() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, "DoesNotExist", null, null, "geheim"));
        });
    }
    
    @Test
    @DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
    @ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithUsernameShouldFailUsernameDoesNotExist2() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, "/L81z0oXEO3vgkU25CCiIw==", null, null, "geheim"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithUsernameShouldFailWrongPassword() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, "PEtEr", null, null, "wrongPassword"));
        });
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
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithMailShouldFailWrongPassword() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, null, null, "peter@sch.mitz", "wrongPassword"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithMailShouldFailMailDoesNotExist() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, null, null, "does@not.exist", "wrongPassword"));
        });
    }
    
    @Test
    @DatabaseSetup(ENCRYPTED_USER_FILE_INITIAL)
    @ExpectedDatabase(value = ENCRYPTED_USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void loginUserWithUsernameShouldFailMailDoesNotExist2() {
        assertThrows(WrongCredentialsException.class, () -> {
            userService.loginUser(new UserDTO(1, "KoBbpLwGdBuAgJDBBIYmfQ==", null, null, "geheim"));
        });
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void updateUserProfileTest() {
        assertTrue(userService.updateProfileDetails(new UserDTO(1,
            "peter",
            "GeänderterPeter",
            "peter@sch.mitz",
            null
        )));
        
        UserDTO changed = userService.getUserDTOFromUsername("peter");
        assertNotNull(changed);
        assertEquals((Integer) 1, changed.getId()); //verify that we got the correct user
        assertEquals("GeänderterPeter", changed.getDisplayname());
        assertEquals("peter@sch.mitz", changed.getMail());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    public void loginShouldFailIfAccountNotConfirmed() {
        assertThrows(UnconfirmedAccountException.class, () -> {
            userService.loginUser(new UserDTO(3, "john", null, null, "geheim"));
        });
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
        UserDTO user = userService.registerUser(new UserDTO(2, "fritz", " fritz", "fritz@fri.tte", "password"));
        assertFalse(userService.getUserFromUsername(user.getUsername()).isAccountConfirmed());
        
        String token = jwtTokenService.generateAccountConfirmationToken(user);
        userService.confirmAccount(token);
        
        assertTrue(userService.getUserFromUsername("fritz").isAccountConfirmed());
    }
    
    @Test
    @DatabaseSetup(value = USER_FILE_INITIAL)
    public void getEmailFromId() {
        assertEquals("peter@sch.mitz", userService.getUserFromId(1).getMail());
    }
}
