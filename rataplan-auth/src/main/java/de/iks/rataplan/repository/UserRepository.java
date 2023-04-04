package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.domain.UserDTO;

public interface UserRepository {
    User findOneByMail(String mail);
    User findOneByUsername(String username);
    User findById(int id);
    boolean existsByMail(String mail);
    boolean existsByUsername(String username);
    UserDTO saveAndFlush(User user);
    void delete(User user);
    void updateUser(User user);
}
