package de.iks.rataplan.runner;

import de.iks.rataplan.domain.EncryptedString;
import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.AppointmentRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Transactional
public class DBEncrypter implements ApplicationRunner {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentRepository appointmentRepository;
    private final VoteParticipantRepository voteParticipantRepository;
    private final CryptoService cryptoService;

    @Override
    public void run(ApplicationArguments args) {
        appointmentRequestRepository.findUnencrypted()
            .peek(appointmentRequest -> {
                ensureEncrypted(appointmentRequest::getTitle, appointmentRequest::setTitle);
                ensureEncrypted(appointmentRequest::getDescription, appointmentRequest::setDescription);
                ensureEncrypted(appointmentRequest::getOrganizerName, appointmentRequest::setOrganizerName);
                ensureEncrypted(appointmentRequest::getOrganizerMail, appointmentRequest::setOrganizerMail);
            })
            .forEach(appointmentRequestRepository::save);
        appointmentRepository.findUnencrypted()
            .peek(appointment -> {
                ensureEncrypted(appointment::getDescription, appointment::setDescription);
                ensureEncrypted(appointment::getUrl, appointment::setUrl);
            })
            .forEach(appointmentRepository::save);
        voteParticipantRepository.findUnencrypted()
            .peek(appointmentMember -> ensureEncrypted(appointmentMember::getName, appointmentMember::setName))
            .forEach(voteParticipantRepository::save);
        appointmentRequestRepository.flush();
        appointmentRepository.flush();
        voteParticipantRepository.flush();
    }

    private boolean ensureEncrypted(Supplier<? extends EncryptedString> sup, Consumer<? super EncryptedString> con) {
        final EncryptedString str = sup.get();
        if(str == null || str.isEncrypted())
            return false;
        con.accept(new EncryptedString(cryptoService.encryptDB(str.getString()), true));
        return true;
    }
}
