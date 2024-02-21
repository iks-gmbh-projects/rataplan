package de.iks.rataplan.restservice;

import de.iks.rataplan.service.CryptoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@RequiredArgsConstructor
public class SigningKeyResolverImpl implements SigningKeyResolver {
    private final CryptoService cryptoService;
    
    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        return cryptoService.getAuthIdKey(claims.getIssuedAt().getTime());
    }
    
    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
        return null;
    }
}
