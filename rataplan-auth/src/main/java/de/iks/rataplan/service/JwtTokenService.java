package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.domain.UserDTO;

import java.util.Date;

public interface JwtTokenService {

	public String getUsernameFromToken(String token);

	public String generateToken(UserDTO user);

	public Date getTokenExpiration(String token);

	public boolean isTokenValid(String token);

	public String generateIdToken();
}
