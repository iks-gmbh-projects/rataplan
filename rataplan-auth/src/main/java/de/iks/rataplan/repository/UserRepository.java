package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;

public interface UserRepository extends BaseUserRepository {
    User saveAndFlush(User user);
}
