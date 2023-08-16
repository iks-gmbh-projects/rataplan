package de.iks.rataplan.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import de.iks.rataplan.restservice.AuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class ConsigneeServiceTest {
    private static final String FILE_PATH = PATH + SERVICE + CONSIGNEES;
    
    @Mock
    private AuthService authService;
    
    private ConsigneeService voteParticipantService;
    
    @Autowired
    private VoteService voteService;
    @Autowired
    private BackendUserAccessRepository backendUserAccessRepository;
    
    @Before
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
        assertEquals("votes.size()", 1, votes.size());
        assertEquals("votes.get(0).getId()", (Integer)1, votes.get(0).getId());
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
