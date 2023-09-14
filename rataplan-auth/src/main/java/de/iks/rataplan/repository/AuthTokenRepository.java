package de.iks.rataplan.repository;

import de.iks.rataplan.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {

    AuthToken findByToken(String token);

    int deleteAllByCreatedDateTimeIsBefore(Date date);

}
