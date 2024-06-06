package de.iks.rataplan.controller;

import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;

import static de.iks.rataplan.testutils.ITConstants.*;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class BackendControllerIT {
    private static final String FILE_PATH = PATH + BACKEND;
    
    @Autowired
    private BackendController backendController;
    
    @Autowired
    private VoteParticipantRepository rawRepository1;
    
    @Autowired
    private VoteRepository rawRepository2;
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testDelete() {
        Assertions.assertEquals(HttpStatus.OK, backendController.deleteData(2).getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + ANONYMIZE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + ANONYMIZE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testAnonymize() {
        Assertions.assertEquals(HttpStatus.OK, backendController.anonymizeData(2).getStatusCode());
        rawRepository1.flush();
        rawRepository2.flush(); //transaction is not automatically flushed by testing environment before comparison
    }
}