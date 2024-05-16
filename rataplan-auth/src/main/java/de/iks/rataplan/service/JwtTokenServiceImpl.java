package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
    public static final String PURPOSE_ACCOUNT_CONFIRMATION = "account confirmation";
    public static final String PURPOSE_RESET_PASSWORD = "reset password";
    private final io.jsonwebtoken.SigningKeyResolver signingKeyResolver;
    
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
    
    @Override
    public String generateAccountConfirmationToken(UserDTO userDTO) {
        Claims claims = Jwts.claims();
        claims.setSubject(userDTO.getId().toString());
        claims.put(CLAIM_PURPOSE, PURPOSE_ACCOUNT_CONFIRMATION);
        claims.setIssuer(jwtConfig.getIssuer());
        int EXPIRATION_TIME = 20000;
        Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        claims.setExpiration(expirationTime);
        return generateToken(claims);
    }
    
    @Override
    public String generateResetPasswordToken(String email) {
        Claims claims = Jwts.claims();
        int expiration = 24*3600*1000;
        claims.setSubject(email);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + expiration));
        claims.put(CLAIM_PURPOSE, PURPOSE_RESET_PASSWORD);
        claims.setIssuer(jwtConfig.getIssuer());
        return generateToken(claims);
    }
    
    @Override
    public String getEmailFromResetPasswordToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if(claims.isEmpty()) throw new InvalidTokenException("token leer");
        if(!claims.get(CLAIM_PURPOSE).equals(PURPOSE_RESET_PASSWORD))
            throw new InvalidTokenException("token ist kein gueltige reset password token");
        if(!claims.getIssuer().equals(jwtConfig.getIssuer()))
            throw new InvalidTokenException("token Aussteller ist ungueltig");
        if(!isTokenValid(token)) throw new InvalidTokenException("token abgelaufen");
        return claims.getSubject();
    }
    
    public Date getTokenExpiration(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    @Override
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(getTokenExpiration(token));
        } catch(Exception e) {
            return false;
        }
    }
    
    @Override
    public Integer getUserIdFromAccountConfirmationToken(String token) {
        final Claims claims;
        try {
            claims = getClaimsFromToken(token);
            
            if(isTokenExpired(claims.getExpiration())) {
                throw new InvalidTokenException("Invalid JWT");
            }
            if(!PURPOSE_ACCOUNT_CONFIRMATION.equals(claims.get(CLAIM_PURPOSE))) {
                throw new InvalidTokenException("Invalid JWT");
            }
            if(!Objects.equals(claims.getIssuer(), jwtConfig.getIssuer())) {
                throw new InvalidTokenException("Invalid JWT");
            }
            
            return Integer.parseInt(claims.getSubject());
        } catch(Exception e) {
            throw new InvalidTokenException("Invalid JWT");
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            
            if(isTokenExpired(claims.getExpiration())) {
                throw new InvalidTokenException("Invalid JWT");
            }
            if(!PURPOSE_LOGIN.equals(claims.get(CLAIM_PURPOSE))) {
                throw new InvalidTokenException("Invalid JWT");
            }
            
            return claims.getSubject();
        } catch(Exception e) {
            throw new InvalidTokenException("Invalid JWT");
        }
    }
    
    @Override
    public Integer getUserIdFromToken(String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            
            if(isTokenExpired(claims.getExpiration())) {
                throw new InvalidTokenException("Invalid JWT");
            }
            if(!PURPOSE_LOGIN.equals(claims.get(CLAIM_PURPOSE))) {
                throw new InvalidTokenException("Invalid JWT");
            }
            
            return claims.get(CLAIM_USERID, Integer.class);
        } catch(Exception e) {
            throw new InvalidTokenException("Invalid JWT");
        }
    }
    private boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
    
    private String generateToken(Claims claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, cryptoService.idKeyP()).compact();
    }
    
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtConfig.getLifetime()*1000);
    }
    
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(cryptoService.idKeyP()).parseClaimsJws(token).getBody();
        } catch(Exception e) {
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
        claims.setExpiration(new Date(System.currentTimeMillis() + 60000));
        return generateToken(claims);
    }
    
    public int getUserIdFromBackendToken(String jwt) {
        Claims claims = getClaimsFromBackendToken(jwt);
        return Integer.parseInt(claims.getSubject());
    }
    private Claims getClaimsFromBackendToken(String jwt) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKeyResolver(signingKeyResolver)
                .requireIssuer("drumdibum-backend")
                .parseClaimsJws(jwt)
                .getBody();
            if(claims.getExpiration().before(new Date())) throw new InvalidTokenException("bad");
            return claims;
        } catch(JwtException ex) {
            throw new InvalidTokenException("bad", ex);
        }
    }
    @Override
    public boolean isBackendTokenValid(String jwt) {
        try {
            getClaimsFromBackendToken(jwt);
        } catch(InvalidTokenException ex) {
            return false;
        }
        return true;
    }
}