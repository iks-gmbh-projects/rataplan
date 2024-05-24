package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@RequiredArgsConstructor
public class RataplanUserDetails implements UserDetails {
    private final int id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final List<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    
    public RataplanUserDetails(User user, CryptoService cryptoService) {
        this(
            user.getId(),
            cryptoService.decryptDB(user.getUsername()),
            cryptoService.decryptDB(user.getMail()),
            user.getPassword(),
            user.isAccountConfirmed()
        );
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}