package de.iks.rataplan.utils;

import static de.iks.rataplan.testutils.TestConstants.DATE_2050_10_10;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_11_11__11_11_00;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_12_12__12_12_00;
import static de.iks.rataplan.testutils.TestConstants.IKS_NAME;
import static de.iks.rataplan.testutils.TestConstants.IKS_MAIL;
import static de.iks.rataplan.testutils.TestConstants.createSimpleVote;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import de.iks.rataplan.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
public class VoteOptionRequestBuilderTest {
    
    @Test
    public void testVoteOptionListWithSimpleNewOptions() {
        Vote vote = createSimpleVote();
        
        List<VoteOption> voteOptions = VoteBuilder.voteOptionList(new VoteOption(new EncryptedString("homeoffice",
                false
            ), vote),
            new VoteOption(new EncryptedString("somewhere", false), vote),
            new VoteOption(new EncryptedString("here", false), vote),
            new VoteOption(new EncryptedString("iks Hilden", false), vote)
        );
        
        for(VoteOption voteOption : voteOptions) {
            assertEquals(vote, voteOption.getVote());
        }
        
        assertEquals("homeoffice", voteOptions.get(0).getDescription().getString());
        assertEquals("somewhere", voteOptions.get(1).getDescription().getString());
        assertEquals("here", voteOptions.get(2).getDescription().getString());
        assertEquals("iks Hilden", voteOptions.get(3).getDescription().getString());
    }
    
    @Test
    public void testVoteOptionListWithSimpleExistingOptions() {
        Vote vote = createSimpleVote();
        
        VoteOption voteOption0 = new VoteOption(new EncryptedString("homeoffice", false), vote);
        VoteOption voteOption1 = new VoteOption(new EncryptedString("somewhere", false), vote);
        VoteOption voteOption2 = new VoteOption(new EncryptedString("here", false), vote);
        VoteOption voteOption3 = new VoteOption(new EncryptedString("iks Hilden", false), vote);
        
        List<VoteOption> voteOptions = VoteBuilder.voteOptionList(voteOption0, voteOption1, voteOption2, voteOption3);
        
        for(VoteOption voteOption : voteOptions) {
            assertEquals(vote, voteOption.getVote());
        }
        
        assertEquals(voteOption0, voteOptions.get(0));
        assertEquals(voteOption1, voteOptions.get(1));
        assertEquals(voteOption2, voteOptions.get(2));
        assertEquals(voteOption3, voteOptions.get(3));
    }
    
    @Test
    public void testVoteOptionListWithComplicatedExistingOptions() {
        Vote vote = this.createComplicatedVote();
        VoteOption voteOption0 = new VoteOption(new EncryptedString("I was first", false), vote);
        voteOption0.setUrl(new EncryptedString("www.nice.url", false));
        voteOption0.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
        voteOption0.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("I was second", false), vote);
        voteOption1.setUrl(new EncryptedString("www.maybe.here", false));
        voteOption1.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
        voteOption1.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
        
        VoteOption voteOption2 = new VoteOption(new EncryptedString("I was last", false), vote);
        voteOption2.setUrl(new EncryptedString("www.test.de", false));
        voteOption2.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
        voteOption2.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
        
        List<VoteOption> voteOptions = VoteBuilder.voteOptionList(voteOption0, voteOption1, voteOption2);
        
        for(VoteOption voteOption : voteOptions) {
            assertEquals(vote, voteOption.getVote());
        }
        
        assertEquals(voteOption0, voteOptions.get(0));
        assertEquals(voteOption1, voteOptions.get(1));
        assertEquals(voteOption2, voteOptions.get(2));
    }
    
    @Test
    public void testParticipantListWithNewParticipants() {
        Vote vote = createSimpleVote();
        List<VoteParticipant> voteParticipants =
            VoteBuilder.voteParticipantList(new VoteParticipant(new EncryptedString("Fritz",
                false
            ), vote),
            new VoteParticipant(new EncryptedString("Hans", false), vote),
            new VoteParticipant(new EncryptedString("Peter", false), vote)
        );
        
        for(VoteParticipant voteParticipant : voteParticipants) {
            assertEquals(vote, voteParticipant.getVote());
        }
        
        assertEquals("Fritz", voteParticipants.get(0).getName().getString());
        assertEquals("Hans", voteParticipants.get(1).getName().getString());
        assertEquals("Peter", voteParticipants.get(2).getName().getString());
    }
    
    @Test
    public void testParticipantListWithExistingParticipants() {
        Vote vote = createSimpleVote();
        
        VoteParticipant voteParticipant0 = new VoteParticipant(new EncryptedString("Fritz", false), vote);
        VoteParticipant voteParticipant1 = new VoteParticipant(new EncryptedString("Hans", false), vote);
        VoteParticipant voteParticipant2 = new VoteParticipant(new EncryptedString("Peter", false), vote);
        List<VoteParticipant> voteParticipants = VoteBuilder.voteParticipantList(voteParticipant0,
            voteParticipant1,
            voteParticipant2
        );
        
        for(VoteParticipant voteParticipant : voteParticipants) {
            assertEquals(vote, voteParticipant.getVote());
        }
        
        assertEquals(voteParticipant0, voteParticipants.get(0));
        assertEquals(voteParticipant1, voteParticipants.get(1));
        assertEquals(voteParticipant2, voteParticipants.get(2));
    }
    
    private Vote createComplicatedVote() {
        return new Vote(new EncryptedString("Coding Dojo", false),
            new EncryptedString("Fun with code", false),
            new Date(DATE_2050_10_10),
            new EncryptedString(IKS_NAME, false),
            new VoteNotificationSettings(IKS_MAIL.getBytes(StandardCharsets.UTF_8), true, false, true),
            new VoteConfig(new VoteOptionConfig(true, true, true, true, true, true), DecisionType.EXTENDED)
        );
    }
}
