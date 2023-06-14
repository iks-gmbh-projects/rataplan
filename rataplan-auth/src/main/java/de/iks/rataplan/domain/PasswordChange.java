package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChange {

	private String oldPassword;
	private String newPassword;
	
}
