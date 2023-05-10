package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByMailAndEncrypted(String mail, boolean encrypted);
    Optional<User> findOneByUsernameAndEncrypted(String username, boolean encrypted);
    boolean existsByMailAndEncrypted(String mail, boolean encrypted);
    boolean existsByUsernameAndEncrypted(String username, boolean encrypted);
    Stream<User> findByEncrypted(boolean encrypted);

    Optional<User> findOneByUsername(String username);

    Optional<User> findOneByMail(String mail);

}
