package de.iks.rataplan.testutils;

import de.iks.rataplan.dto.UserDTO;

public final class ITConstants {

	public static final String FILE_INITIAL = "/initial.xml";
	public static final String FILE_EXPECTED= "/expected.xml";
    
    public static final String VERSION = "/v1";
	public static final String USERS = VERSION + "/users";
    public static final String CONTACTS = VERSION + "/contacts";

	public static final UserDTO USER_1 = new UserDTO(null, "fritz", "fritz", "fritz@fri.tte", "password");
    public static final UserDTO USER_2 = new UserDTO(1, "peter", "peter", "peter@sch.mitz");
}
