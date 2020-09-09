package de.iks.rataplan.testutils;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.FrontendUser;

public final class ITConstants {

	// Files
	public static final String FILE_EMPTY_DB = "classpath:integration/db/empty_DB.xml";
	public static final String FILE_EXPECTED = "/expected.xml";
	public static final String FILE_INITIAL = "/initial.xml";

	// Filepath to resources from integration tests (controller)
	public static final String PATH = "classpath:integration/db/controller";

	// Paths (used to find files and do REST-calls -> folder structure is same as REST-call structure)
	public static final String APPOINTMENTMEMBERS = "/appointmentMembers";
	public static final String APPOINTMENTREQUESTS = "/appointmentRequests";
	public static final String CONTACTS = "/contacts";
	public static final String CREATE = "/create";
	public static final String CREATIONS = "/creations";
	public static final String DELETE = "/delete";
	public static final String EDIT = "/edit";
	public static final String GET = "/get";
	public static final String JWTTOKEN = "/jwttoken";
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

	public static final AuthUser AUTHUSER_1 = new AuthUser(1, IKS_MAIL, "IKS_1", "password", "firstname", "lastname");
	public static final AuthUser AUTHUSER_2 = new AuthUser(2, IKS_MAIL, "IKS_2", "pass", "first", "last");
	public static final AuthUser AUTHUSER_3 = new AuthUser(3, IKS_MAIL, "IKS_3", "p", "f", "l");

	public static final FrontendUser FRONTENDUSER_1 = new FrontendUser(1, IKS_MAIL, "IKS_1", null, "firstname",
			"lastname");
	public static final FrontendUser FRONTENDUSER_2 = new FrontendUser(2, IKS_MAIL, "IKS_2", null, "first", "last");

	public static final FrontendUser FRONTENDUSER_1_NEW = new FrontendUser(null, IKS_MAIL, "IKS_1", "password",
			"firstname", "lastname");
	public static final FrontendUser FRONTENDUSER_2_NEW = new FrontendUser(null, IKS_MAIL, "IKS_2", "pass", "first",
			"last");

	public static final long DATE_2050_10_10 = 2549010652L * 1000;

}
