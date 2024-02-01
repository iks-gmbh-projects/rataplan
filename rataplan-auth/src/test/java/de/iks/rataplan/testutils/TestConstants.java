package de.iks.rataplan.testutils;

import de.iks.rataplan.domain.User;

import java.nio.charset.StandardCharsets;

public final class TestConstants {

	public static final String ACTIVE_PROFILE_TEST = "test";
	public static final String FILE_TEST_PROPERTIES = "classpath:/test.properties";

	public static final String FILE_INITIAL = "/initial.xml";
	public static final String FILE_EXPECTED = "/expected.xml";

	public static final String PATH = "classpath:integration/db/controller";
	
	public static final User USER_1 = new User(null, "fritz@fri.tte".getBytes(StandardCharsets.UTF_8), "fritz".getBytes(
        StandardCharsets.UTF_8), "password", "fritz".getBytes(StandardCharsets.UTF_8));
}
