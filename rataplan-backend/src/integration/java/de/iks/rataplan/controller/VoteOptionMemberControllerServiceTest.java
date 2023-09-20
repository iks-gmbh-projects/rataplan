package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.CryptoService;
import de.iks.rataplan.utils.CookieBuilder;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static de.iks.rataplan.testutils.ITConstants.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(
    value = {DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
public class VoteOptionMemberControllerServiceTest {
    
    private static final String FILE_PATH = PATH + VOTE_PARTICIPANTS;
    
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    @Autowired
    private MockMvc mockMvc;
    private MockRestServiceServer mockRestServiceServer;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private CryptoService cryptoService;
    
    @Autowired
    private CookieBuilder cookieBuilder;
    
    @Autowired
    private RestOperations restOperations;

    @BeforeEach
    public void setUp() {
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        when(cryptoService.encryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(cryptoService.decryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
    }
    
    @Test
    public void loadsContext() {
        Assertions.assertNotNull(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void addVoteParticipant() throws Exception {
        
        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        
        String json = gson.toJson(voteParticipantDTO);
        
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.
            post(VERSION + VOTES + "/1/participants");
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());
        
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void addVoteParticipantWithUserId() throws Exception {
        
        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        voteParticipantDTO.setUserId(1);
        
        String json = gson.toJson(voteParticipantDTO);
        
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + VOTES + "/1/participants");
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());
        
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + CREATE + JWTTOKEN + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void addVoteParticipantWithJwtToken() throws Exception {
        this.setMockRestServiceServer(AUTHUSER_1, "hans");
        
        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        voteParticipantDTO.setName(AUTHUSER_1.getUsername());
        
        String json = gson.toJson(voteParticipantDTO);
        
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + VOTES + "/1/participants");
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());
        requestBuilder.cookie(cookieBuilder.generateCookie(JWTTOKEN_VALUE, false));
        
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + PARTICIPATE_TWICE + FILE_INITIAL)
    public void addVoteParticipantWithJwtTokenTwice() throws Exception {
        this.setMockRestServiceServer(AUTHUSER_1, "hans");
        
        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        voteParticipantDTO.setName(AUTHUSER_1.getUsername());
        
        String json = gson.toJson(voteParticipantDTO);
        
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + VOTES + "/1/participants");
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());
        requestBuilder.cookie(cookieBuilder.generateCookie(JWTTOKEN_VALUE, false));
        
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    private VoteParticipantDTO createSimpleVoteParticipant() {
        VoteParticipantDTO voteParticipantDTO = new VoteParticipantDTO();
        voteParticipantDTO.setVoteId(1);
        voteParticipantDTO.setName("IKS");
        
        List<VoteDecisionDTO> decisions = new ArrayList<>();
        
        decisions.add(new VoteDecisionDTO(1, 1, 0, null));
        decisions.add(new VoteDecisionDTO(2, 1, 1, null));
        
        voteParticipantDTO.setDecisions(decisions);
        return voteParticipantDTO;
    }
    
    private void setMockRestServiceServer(AuthUser authUser, String displayName) {
        given(authService.getUserData(JWTTOKEN_VALUE)).willReturn(authUser);
        if(displayName != null) given(authService.fetchDisplayName(authUser.getId())).willReturn(displayName);
    }
}
