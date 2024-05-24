package de.iks.rataplan.controller;

import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.service.CryptoServiceImpl;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.nio.charset.StandardCharsets;

import static de.iks.rataplan.testutils.ITConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
    listeners = {
        DirtiesContextTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class
    }, mergeMode = MERGE_WITH_DEFAULTS
)
public class RataplanAuthRestControllerIT {
    
    private static final String BASE_LINK = "classpath:integration/db/controller";
    private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;
    
    public static final String REGISTER = USERS + "/register";
    public static final String LOGIN = USERS + "/login";
    public static final String PROFILE = USERS + "/profile";
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private CryptoServiceImpl cryptoService;
    
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
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUser() {
        ResponseEntity<?> response = restTemplate.postForEntity(REGISTER, USER_1, void.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailUsernameAlreadyExists() {
        ResponseEntity<?> response = restTemplate.postForEntity(REGISTER,
            new UserDTO(1, "PeTEr", "peter", "neuerpeter@sch.mitz", "password"),
            void.class
        );
        assertEquals(HttpStatus.GONE, response.getStatusCode());
    }
    
    @Test
    @DatabaseSetup(USER_FILE_INITIAL)
    @ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void registerUserShouldFailMailAlreadyExists() {
        ResponseEntity<?> response = restTemplate.postForEntity(REGISTER,
            new UserDTO(1, "PeTErS", "peter", "pETer@sch.mitz", "password"),
            void.class
        );
        assertEquals(HttpStatus.GONE, response.getStatusCode());
    }
}