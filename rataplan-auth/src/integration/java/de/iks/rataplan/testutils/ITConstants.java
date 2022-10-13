package de.iks.rataplan.testutils;

import de.iks.rataplan.domain.User;

public final class ITConstants {

	public static final String FILE_INITIAL = "/initial.xml";
	public static final String FILE_EXPECTED= "/expected.xml";
	
	public static final String PATH = "classpath:integration/db/controller";
	
	public static final String VERSION = "/v1";
	public static final String USERS = VERSION + "/users";

	public static final User USER_1 = new User(null, "fritz@fri.tte", "fritz", "password", "DerFritz");
	
}
