package de.iks.rataplan.runner;

import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.EncryptedString;
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
    private final CryptoService cryptoService;

    @Override
    public void run(ApplicationArguments args) {
        for(AppointmentRequest appointmentRequest: appointmentRequestRepository.findAll()) {
            boolean altered = ensureEncrypted(appointmentRequest::getTitle, appointmentRequest::setTitle);
            altered |= ensureEncrypted(appointmentRequest::getDescription, appointmentRequest::setDescription);
            altered |= ensureEncrypted(appointmentRequest::getOrganizerName, appointmentRequest::setOrganizerName);
            altered |= ensureEncrypted(appointmentRequest::getOrganizerMail, appointmentRequest::setOrganizerMail);
            for(Appointment appointment: appointmentRequest.getAppointments()) {
                altered |= ensureEncrypted(appointment::getDescription, appointment::setDescription);
                altered |= ensureEncrypted(appointment::getUrl, appointment::setUrl);
            }
            for(AppointmentMember appointmentMember: appointmentRequest.getAppointmentMembers()) {
                altered|= ensureEncrypted(appointmentMember::getName, appointmentMember::setName);
            }
            if(altered) {
                appointmentRequestRepository.save(appointmentRequest);
            }
        }
        appointmentRequestRepository.flush();
    }

    private boolean ensureEncrypted(Supplier<? extends EncryptedString> sup, Consumer<? super EncryptedString> con) {
        final EncryptedString str = sup.get();
        if(str == null || str.isEncrypted())
            return false;
        con.accept(new EncryptedString(cryptoService.encryptDB(str.getString()), true));
        return true;
    }
}
