package de.iks.rataplan.testutils;

import de.iks.rataplan.domain.AuthUser;

public final class ITConstants {

	// Files
	public static final String FILE_EMPTY_DB = "classpath:integration/db/empty_DB.xml";
	public static final String FILE_EXPECTED = "/expected.xml";
	public static final String FILE_INITIAL = "/initial.xml";

	// Filepath to resources from integration tests (controller)
	public static final String PATH = "classpath:integration/db/controller";
    public static final String BACKEND = "/backend";
    public static final String ANONYMIZE = "/anonymize";

	// Paths (used to find files and do REST-calls -> folder structure is same as REST-call structure)
	public static final String VOTE_PARTICIPANTS = "/voteParticipants";
	public static final String VOTES = "/votes";
	public static final String CONTACTS = "/contacts";
	public static final String CREATE = "/create";
	public static final String CREATIONS = "/creations";
	public static final String DELETE = "/delete";
	public static final String EDIT = "/edit";
	public static final String GET = "/get";
	public static final String JWTTOKEN = "/jwttoken";
	public static final String PARTICIPATE_TWICE = "/participateTwice";
	public static final String LOGIN = "/login";
	public static final String LOGOUT = "/logout";
	public static final String PARTICIPATIONS = "/participations";
	public static final String PASSWORD = "/password";
	public static final String PROFILE = "/profile";
	public static final String REGISTER = "/register";
	public static final String UPDATE = "/update";
	public static final String USERS = "/users";
	public static final String VERSION = "/v1";

	// URL to mock
	public static final String AUTH_SERVICE_URL = "http://localhost:8081/v1";

	// Passwords (encrypted in database)
	public static final String ACCESS_TOKEN_ADMIN_PASSWORD = "adminpassword";
	public static final String ACCESS_TOKEN_PASSWORD = "password";
	public static final String ACCESS_TOKEN_WRONG_PASSWORD = "wrongpassword";

	// Header
	public static final String HEADER_ACCESS_TOKEN = "accesstoken";
	public static final String JWTTOKEN_VALUE = "my_jwt_token";

	// Cookie
	public static final String COOKIE_JWTTOKEN = "jwttoken";
	public static final Integer COOKIE_MAX_AGE = 60000;

	// Static Objects
	public static final String IKS_MAIL = "iks@iks-gmbh.com";

	public static final AuthUser AUTHUSER_1 = new AuthUser(1, "IKS_1");
	public static final AuthUser AUTHUSER_2 = new AuthUser(2, "IKS_2");
	public static final AuthUser AUTHUSER_3 = new AuthUser(3, "IKS_3");

	public static final long DATE_2050_10_10 = 2549010652L * 1000;

}
