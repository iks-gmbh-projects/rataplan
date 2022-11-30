package de.iks.rataplan.repository;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final RawUserRepository userRepository;
    private final CryptoService cryptoService;

    @Override
    public User findOneByMail(String mail) {
        return cryptoService.ensureDecrypted(
            userRepository.findOneByMail(
                cryptoService.encryptDB(mail)
            )
        );
    }

    @Override
    public User findOneByUsername(String username) {
        return cryptoService.ensureDecrypted(
            userRepository.findOneByUsername(
                cryptoService.encryptDB(username)
            )
        );
    }

    @Override
    public User findById(int id) {
        return cryptoService.ensureDecrypted(
            userRepository.findById(id)
        );
    }

    @Override
    public boolean existsByMail(String mail) {
        return userRepository.existsByMail(
            cryptoService.encryptDB(mail)
        );
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(
            cryptoService.encryptDB(username)
        );
    }

    @Override
    public User saveAndFlush(User user) {
        return cryptoService.ensureDecrypted(
            userRepository.saveAndFlush(
                cryptoService.ensureEncrypted(user)
            )
        );
    }
}
