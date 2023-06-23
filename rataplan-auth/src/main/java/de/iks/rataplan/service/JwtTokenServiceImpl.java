package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService, Serializable {
	private final CryptoService cryptoService;
	private final JwtConfig jwtConfig;
	public static final String CLAIM_PURPOSE = "purpose";
	public static final String CLAIM_USERID = "user_id";
	public static final String PURPOSE_LOGIN = "login";
	public static final String PURPOSE_ID = "id";

	@Override
	public String generateLoginToken(UserDTO user) {
		Claims claims = Jwts.claims();
		claims.setSubject(user.getUsername());
		claims.put(CLAIM_USERID, user.getId());
		claims.setIssuedAt(new Date());
		claims.setIssuer(jwtConfig.getIssuer());
		claims.put(CLAIM_PURPOSE, PURPOSE_LOGIN);
		claims.setExpiration(generateExpirationDate());
		return generateToken(claims);
	}

	public Date getTokenExpiration(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims.getExpiration();
	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			return !isTokenExpired(getTokenExpiration(token));
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
	}

	@Override
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);

			if (isTokenExpired(claims.getExpiration())) {
				throw new InvalidTokenException("Invalid JWT");
			}
			if(!PURPOSE_LOGIN.equals(claims.get(CLAIM_PURPOSE))) {
				throw new InvalidTokenException("Invalid JWT");
			}

			username = claims.getSubject();
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
		return username;
	}

	private boolean isTokenExpired(Date expiration) {
		return expiration.before(new Date());
	}

	private String generateToken(Claims claims) {
		return Jwts.builder().setClaims(claims)
				.signWith(SignatureAlgorithm.RS512, cryptoService.idKeyP()).compact();
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + jwtConfig.getLifetime() * 1000);
	}

	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(cryptoService.idKeyP()).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
		return claims;
	}

	@Override
	public String generateIdToken() {
		Claims claims = Jwts.claims();
		claims.setIssuedAt(new Date());
		claims.setSubject(jwtConfig.getIssuer());
		claims.setIssuer(jwtConfig.getIssuer());
		claims.put(CLAIM_PURPOSE, PURPOSE_ID);
		claims.setExpiration(new Date(System.currentTimeMillis()+60000));
		return generateToken(claims);
	}
}
