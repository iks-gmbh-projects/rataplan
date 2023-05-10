package de.iks.rataplan.runner;

import de.iks.rataplan.repository.UserRepository;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBEncrypter implements ApplicationRunner {
    private final UserRepository userRepository;

    private final CryptoService cryptoService;

    @Override
    @Transactional
    public void run(ApplicationArguments applicationArguments) {
        log.info("Encrypting DB");
        userRepository.findByEncrypted(false)
            .peek(user -> {
                user.setUsername(user.getUsername().toLowerCase());
                user.setMail(user.getMail().toLowerCase());
            })
            .map(cryptoService::ensureEncrypted)
            .forEach(userRepository::save);
        log.info("Done");
    }
}
