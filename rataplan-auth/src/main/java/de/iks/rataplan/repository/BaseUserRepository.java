package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;

public interface BaseUserRepository {
    User findOneByMail(String mail);

    User findOneByUsername(String username);

    User findById(int id);

    boolean existsByMail(String mail);

    boolean existsByUsername(String username);
}
