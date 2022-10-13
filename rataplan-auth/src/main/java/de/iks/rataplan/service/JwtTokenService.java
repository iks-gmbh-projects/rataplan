package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;

import java.util.Date;

public interface JwtTokenService {

	public String getUsernameFromToken(String token);

	public String generateToken(User user);

	public Date getTokenExpiration(String token);

	public boolean isTokenValid(String token);
}
