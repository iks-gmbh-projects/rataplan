package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByMail(byte[] mail);
    Optional<User> findOneByUsername(byte[] username);
    Stream<User> findByDisplayname(byte[] displayname);
    boolean existsByMail(byte[] mail);
    boolean existsByUsername(byte[] username);

}
