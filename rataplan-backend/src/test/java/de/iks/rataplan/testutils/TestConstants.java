package de.iks.rataplan.testutils;

import static de.iks.rataplan.utils.VoteBuilder.voteOptionList;

import java.nio.charset.StandardCharsets;
import java.sql.Date;

import de.iks.rataplan.domain.*;

public final class TestConstants {
    
    // Files
    public static final String FILE_EMPTY_DB = "classpath:test/db/empty_DB.xml";
    public static final String FILE_EXPECTED = "/expected.xml";
    public static final String FILE_INITIAL = "/initial.xml";
    
    // Filepath to resources from unit tests
    public static final String PATH = "classpath:test/db";
    
    // Paths (used to find files)
    public static final String CONSIGNEES = "/consignees";
    public static final String TRANSCRIBE_CONSIGNEES_TO_BACKEND_USER_ACCESSES =
        "/transcribeConsigneesToBackendUserAccesses";
    public static final String VOTE_PARTICIPANTS = "/voteParticipants";
    public static final String VOTES = "/votes";
    public static final String AUTHORIZATION = "/authorization";
    public static final String CONTROLLERSERVICE = "/controllerService";
    public static final String GENERATORTOKEN = "/generatorToken";
    public static final String CREATE = "/create";
    public static final String DECISION = "/decision";
    public static final String DELETE = "/delete";
    public static final String EXPIRED = "/expired";
    public static final String GET = "/get";
    public static final String PARTICIPANTS = "/participants";
    public static final String REPOSITORY = "/repository";
    public static final String SERVICE = "/service";
    public static final String UPDATE = "/update";
    
    // URL to mock
    public static final String AUTH_SERVICE_URL = "http://localhost:8081/v1";
    
    // Dates, translated in database
    public static final long DATE_2050_10_10 = 2549010652L*1000;
    public static final long DATE_2050_11_11__11_11_00 = 2551770660L*1000;
    public static final long DATE_2050_12_12__12_12_00 = 2554452720L*1000;
    
    // Header
    public static final String HEADER_JWTTOKEN = "jwttoken";
    public static final String RETURNED_JWTTOKEN = "returned token";
    public static final String ENTERED_JWTTOKEN = "entered token";
    
    // Passwords (encrypted in database)
    public static final String ACCESS_TOKEN_ADMIN_PASSWORD = "adminpassword";
    public static final String ACCESS_TOKEN_PASSWORD = "password";
    public static final String ACCESS_TOKEN_WRONG_PASSWORD = "wrongpassword";
    
    // Static Objects
    public static final String IKS_NAME = "IKS GmbH";
    public static final String IKS_MAIL = "iks@iks-gmbh.com";
    
    public static final AuthUser AUTHUSER_1 = new AuthUser(1, "IKS_1");
    public static final AuthUser AUTHUSER_2 = new AuthUser(2, "IKS_2");
    public static final AuthUser AUTHUSER_3 = new AuthUser(3, "IKS_3");
    
    //public static final BackendUser BACKENDUSER_1_NEW = new BackendUser(1);
    
    public static Vote createSimpleVote() {
        Vote vote = new Vote(new EncryptedString("Coding Dojo", false),
            new EncryptedString("Fun with code", false),
            new Date(DATE_2050_10_10),
            new EncryptedString(IKS_NAME, false),
            new VoteNotificationSettings(IKS_MAIL.getBytes(StandardCharsets.UTF_8), true, false, true),
            new VoteConfig(new VoteOptionConfig(true, false, false, false, false, false),
                DecisionType.DEFAULT,
                false,
                null
            )
        );
        
        vote.setOptions(voteOptionList(new VoteOption(new EncryptedString("iks Hilden", false), vote),
            new VoteOption(new EncryptedString("homeoffice", false), vote, false, null)
        ));
        return vote;
    }
}
