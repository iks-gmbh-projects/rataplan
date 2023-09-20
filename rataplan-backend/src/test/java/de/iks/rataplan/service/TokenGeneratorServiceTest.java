package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.VoteRepository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.*;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class TokenGeneratorServiceTest {

    private static final String FILE_PATH = PATH + CONTROLLERSERVICE + GENERATORTOKEN;

    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    public void checkValidFormParticipationToken() throws Exception {
        String token = tokenGeneratorService.generateToken(8);
        assertTrue(String.format("Invalid Token: %s", token), token.matches("[a-zA-Z0-9]{8}"));
    }

    @Test
    public void checkValidFormEditToken() throws Exception {
        String token = tokenGeneratorService.generateToken(10);
        assertTrue(String.format("Invalid Token: %s", token), token.matches("[a-zA-Z0-9]{10}"));
    }

    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void generateParticipationToken() throws Exception {
        Vote vote = createSimpleVote();

        String token = tokenGeneratorService.generateToken(8);
        vote.setParticipationToken(token);
        voteRepository.saveAndFlush(vote);

        Vote createdVote = voteRepository.findByParticipationToken(token);
        assertNotNull(createdVote.getParticipationToken());
    }

    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void generateEditToken() throws Exception {
        Vote vote = createSimpleVote();

        String token = tokenGeneratorService.generateToken(10);
        vote.setEditToken(token);
        voteRepository.saveAndFlush(vote);

        Vote createdVote = voteRepository.findByEditToken(token);
        assertNotNull(createdVote.getEditToken());
    }

    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    public void TestIfTokeIsUnique() throws Exception {
        assertTrue(tokenGeneratorService.isTokenUnique("coding02",8));
        assertFalse(tokenGeneratorService.isTokenUnique("coding01",8));
    }

    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    public void generateNewTokenWhenExistsInDB() throws Exception {
        String token;
        if (!tokenGeneratorService.isTokenUnique("coding01", 8)) {
            token = tokenGeneratorService.generateToken(8);
            assertNotEquals("coding01", token);
        }
    }
}
