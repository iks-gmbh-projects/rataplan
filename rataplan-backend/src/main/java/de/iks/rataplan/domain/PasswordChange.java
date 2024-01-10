package de.iks.rataplan.domain;

import lombok.Data;

@Data
public class PasswordChange {
	private String oldPassword;
	private String newPassword;
}
