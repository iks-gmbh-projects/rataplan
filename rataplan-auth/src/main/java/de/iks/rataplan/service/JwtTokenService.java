package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;

public interface JwtTokenService {

	public String getUsernameFromToken(String token);

	public String generateToken(User user);

	public boolean isTokenValid(String token);
}
