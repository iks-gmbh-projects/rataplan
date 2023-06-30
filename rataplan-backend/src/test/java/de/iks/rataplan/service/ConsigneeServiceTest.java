package de.iks.rataplan.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.Vote;
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

import java.util.List;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class ConsigneeServiceTest {
    private static final String FILE_PATH = PATH + SERVICE + CONSIGNEES;
    
    @Autowired
    private ConsigneeService voteParticipantService;
    
    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testGetVotesForConsignee() {
        List<Vote> votes = voteParticipantService.getVotesForConsignee(AUTHUSER_1);
        assertEquals("votes.size()", 1, votes.size());
        assertEquals("votes.get(0).getId()", (Integer)1, votes.get(0).getId());
    }
}
