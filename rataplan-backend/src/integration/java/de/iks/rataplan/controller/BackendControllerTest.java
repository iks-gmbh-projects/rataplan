package de.iks.rataplan.controller;

import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;
import de.iks.rataplan.restservice.AuthService;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class BackendControllerTest {
    private static final String FILE_PATH = PATH + CONTROLLER + BACKEND;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private BackendController backendController;
    
    @Autowired
    private VoteParticipantRepository rawRepository1;
    
    @Autowired
    private VoteRepository rawRepository2;
    
    @BeforeEach
    public void mockSecret() {
        when(authService.isValidIDToken(anyString())).thenAnswer(invocation -> "validToken".equals(invocation.getArgument(0, String.class)));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDelete() {
        Assertions.assertEquals(HttpStatus.OK, backendController.deleteData(2, "validToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDeleteBadToken() {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, backendController.deleteData(2, "badToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testAnonymize() {
        Assertions.assertEquals(HttpStatus.OK, backendController.anonymizeData(2, "validToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testAnonymizeBadToken() {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, backendController.anonymizeData(2, "badToken").getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
}
