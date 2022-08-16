package de.iks.rataplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.iks.rataplan.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("select u from User as u where LOWER(mail) = LOWER(?1)")
    User findOneByMail(String mail);

    @Query("select u from User as u where LOWER(username) = LOWER(?1)")
    User findOneByUsername(String username);

    User findById(int id);

    boolean existsByMail(String mail);

    boolean existsByUsername(String username);

}
