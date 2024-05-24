package de.iks.rataplan.service;

import lombok.RequiredArgsConstructor;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MultiIssuerJWSKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {
    private final Predicate<String> trusted;
    private final Map<String, JWSKeySelector<SecurityContext>> resolved = new ConcurrentHashMap<>();
    
    @Override
    public List<? extends Key> selectKeys(JWSHeader header, JWTClaimsSet claimsSet, SecurityContext context) throws
        KeySourceException
    {
        String issuer = claimsSet.getIssuer().replaceFirst("/+$", "");
        if(trusted.test(issuer)) {
            return resolved.computeIfAbsent(issuer, this::createSelector)
                .selectJWSKeys(header, context);
        }
        return List.of();
    }
    
    private JWSKeySelector<SecurityContext> createSelector(String issuer) {
        try {
            JWKSet keySet = JWKSet.load(new URL(issuer+"/jwks"));
            Set<JWSAlgorithm> algorithms = keySet.getKeys()
                .stream()
                .map(JWK::getAlgorithm)
                .filter(Objects::nonNull)
                .map(Algorithm::toString)
                .map(JWSAlgorithm::parse)
                .collect(Collectors.toUnmodifiableSet());
            return new JWSVerificationKeySelector<>(algorithms, new ImmutableJWKSet<>(keySet));
        } catch(IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}