package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final RawUserRepository userRepository;
    private final CryptoService cryptoService;

    @Override
    public User findOneByMail(String cmail) {
        final String mail = cmail.toLowerCase();
        return userRepository.findOneByMailAndEncrypted(cryptoService.encryptDB(mail), true).orElseGet(() -> userRepository.findOneByMailAndEncrypted(mail, false).orElse(null));
    }

    @Override
    public User findOneByUsername(String cusername) {
        final String username = cusername.toLowerCase();
        return userRepository.findOneByUsernameAndEncrypted(cryptoService.encryptDB(username), true).orElseGet(() -> userRepository.findOneByUsernameAndEncrypted(username, false).orElse(null));
    }

    @Override
    public User findById(int id) {
        return userRepository.findOne(id);
    }

    @Override
    public boolean existsByMail(String mail) {
        mail = mail.toLowerCase();
        return userRepository.existsByMailAndEncrypted(cryptoService.encryptDB(mail), true) || userRepository.existsByMailAndEncrypted(mail, false);
    }

    @Override
    public boolean existsByUsername(String username) {
        username = username.toLowerCase();
        return userRepository.existsByUsernameAndEncrypted(cryptoService.encryptDB(username), true) || userRepository.existsByUsernameAndEncrypted(username, false);
    }

    @Override
    public User saveAndFlush(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setMail(user.getMail().toLowerCase());
        if (user.getId() == null && (userRepository.existsByUsernameAndEncrypted(user.getUsername(), false) || userRepository.existsByMailAndEncrypted(user.getMail(), false))) {
            throw new DataIntegrityViolationException("Username or Email already in use");
        }
        return userRepository.saveAndFlush(cryptoService.ensureEncrypted(user));
    }

    public void updateUser(User user){
        if (user.getId() == null && (userRepository.existsByUsernameAndEncrypted(user.getUsername(), false)
                || userRepository.existsByMailAndEncrypted(user.getMail(), false))) {
            throw new DataIntegrityViolationException("Username or Email already in use");
        }else {
            userRepository.saveAndFlush(user);
        }
    }



    @Override
    public void delete(User user) {
        userRepository.delete(user.getId());
    }
}
