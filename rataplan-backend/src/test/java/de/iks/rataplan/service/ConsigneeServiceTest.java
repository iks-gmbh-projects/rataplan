package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import de.iks.rataplan.restservice.AuthService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestExecutionListeners(
    value = {DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
public class ConsigneeServiceTest {
    private static final String FILE_PATH = PATH + SERVICE + CONSIGNEES;
    
    @Mock
    private AuthService authService;
    @Autowired
    private ConsigneeService voteParticipantService;
    
    @Autowired
    private VoteService voteService;
    @Autowired
    private BackendUserAccessRepository backendUserAccessRepository;
    
    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        when(authService.fetchUserIdFromEmail("user2@drumdibum.test"))
            .thenReturn(2);
        voteParticipantService = new ConsigneeServiceImpl(backendUserAccessRepository, authService, voteService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testGetVotesForConsignee() {
        List<Vote> votes = voteParticipantService.getVotesForConsignee(AUTHUSER_1);
        Assertions.assertEquals(1, votes.size(), "votes.size()");
        Assertions.assertEquals((Integer)1, votes.get(0).getId(), "votes.get(0).getId()");
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + TRANSCRIBE_CONSIGNEES_TO_BACKEND_USER_ACCESSES + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testTranscribeConsigneesToBackendUserAccesses() {
        Vote vote = voteService.getVoteById(1);
        vote.setConsigneeList(Collections.singletonList("user2@drumdibum.test"));
        voteParticipantService.transcribeConsigneesToBackendUserAccesses(vote);
    }
}
