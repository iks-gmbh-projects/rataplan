package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface RawUserRepository extends BaseUserRepository, JpaRepository<User, String> {
    Stream<User> findByEncrypted(boolean encrypted);
}
