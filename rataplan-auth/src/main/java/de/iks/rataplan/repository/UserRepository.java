package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;

public interface UserRepository {
    User findOneByMail(String mail);
    User findOneByUsername(String username);
    User findById(int id);
    boolean existsByMail(String mail);
    boolean existsByUsername(String username);
    User saveAndFlush(User user);
    void delete(User user);
}
