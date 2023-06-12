package de.iks.rataplan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontendUser {
	private Integer id;
	private String mail;
	private String username;
    private String password;
	private String displayname;

	public FrontendUser(AuthUser authUser) {
		this.id = authUser.getId();
		this.mail = authUser.getMail();
		this.username = authUser.getUsername();
		this.password = null;
		this.displayname = authUser.getDisplayname();
	}
}
