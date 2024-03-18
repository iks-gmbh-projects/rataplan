package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.domain.notifications.*;
import de.iks.rataplan.dto.NotificationDTO;
import de.iks.rataplan.dto.NotificationSettingsDTO;
import de.iks.rataplan.repository.NotificationCategoryRepository;
import de.iks.rataplan.repository.NotificationRepository;
import de.iks.rataplan.repository.NotificationTypeRepository;
import de.iks.rataplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationCategoryRepository notificationCategoryRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;
    
    private final UserRepository userRepository;
    
    private final CryptoService cryptoService;
    private final MailService mailService;
    @Override
    @Transactional(readOnly = true)
    public NotificationSettingsDTO getNotificationSettings(int userId) {
        return userRepository.findById(userId).map(NotificationServiceImpl::getNotificationSettings).orElse(null);
    }
    
    private static NotificationSettingsDTO getNotificationSettings(User user) {
        NotificationSettingsDTO ret = new NotificationSettingsDTO();
        ret.setDefaultSettings(user.getDefaultEmailCycle());
        ret.setCategorySettings(user.getNotificationCategorySettings()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey().getName(), Entry::getValue)));
        ret.setTypeSettings(user.getNotificationTypeSettings()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)));
        return ret;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> getNotificationTypes() {
        return notificationCategoryRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(NotificationCategory::getName,
                Collectors.mapping(NotificationCategory::getTypes,
                    Collectors.mapping(Map::values,
                        Collectors.flatMapping(Collection::stream,
                            Collectors.mapping(NotificationType::getName, Collectors.toUnmodifiableList())
                        )
                    )
                )
            ));
    }
    @Override
    @Transactional
    public NotificationSettingsDTO updateNotificationSettings(int userId, NotificationSettingsDTO settings) {
        User user = userRepository.getReferenceById(userId);
        if(settings.getDefaultSettings() != null) user.setDefaultEmailCycle(settings.getDefaultSettings());
        if(settings.getCategorySettings() != null) user.setNotificationCategorySettings(settings.getCategorySettings()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> notificationCategoryRepository.findByName(e.getKey()).orElseThrow(),
                Map.Entry::getValue
            )));
        if(settings.getCategorySettings() != null) user.setNotificationTypeSettings(settings.getTypeSettings()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> notificationTypeRepository.findByName(e.getKey()).orElseThrow(),
                Entry::getValue
            )));
        
        user = userRepository.saveAndFlush(user);
        
        notificationRepository.deleteAllByIdInBatch(notificationRepository.findCycleNotifications(EmailCycle.SUPPRESS)
            .map(Notification::getId)
            .collect(Collectors.toUnmodifiableList()));
        
        sendSummary(Set.of(EmailCycle.INSTANT));
        
        return getNotificationSettings(user);
    }
    
    @Override
    @Transactional
    public void notify(Collection<? extends NotificationDTO> notifications) {
        Map<Boolean, List<Notification>> instantPartition = notifications.stream()
            .map(this::fromDTO)
            .collect(Collectors.groupingBy(Notification::getCycle))
            .entrySet()
            .stream()
            .filter(e -> e.getKey() != EmailCycle.SUPPRESS)
            .collect(Collectors.partitioningBy(e -> e.getKey() == EmailCycle.INSTANT,
                Collectors.flatMapping(e -> e.getValue().stream(), Collectors.toUnmodifiableList())
            ));
        notificationRepository.saveAllAndFlush(instantPartition.get(false));
        instantPartition.get(true)
            .stream()
            .collect(Collectors.groupingBy(
                this::getNotificationMail,
                Collectors.mapping(this::toMailData, Collectors.toUnmodifiableList())
            ))
            .forEach((recipientMail, notifs) -> {
                try {
                    if(notifs.size() == 1) {
                        mailService.sendNotification(recipientMail, notifs.get(0));
                    } else {
                        mailService.sendNotificationSummary(recipientMail, notifs);
                    }
                } catch(RuntimeException ex) {
                    log.error("Unexpected error while sending emails", ex);
                }
            });
    }
    private Notification fromDTO(NotificationDTO notificationDTO) {
        NotificationType type = notificationTypeRepository.findByName(notificationDTO.getType())
            .orElseThrow();
        try {
            if(notificationDTO.getRecipientId() == null) throw new NoSuchElementException();
            return new Notification(notificationDTO.getRecipientEmail(),
                userRepository.findById(notificationDTO.getRecipientId()).orElseThrow(),
                type,
                cryptoService.encryptDB(notificationDTO.getSubject()),
                cryptoService.encryptDB(notificationDTO.getSummaryContent()),
                notificationDTO.getContent()
            );
        } catch(NoSuchElementException ex) {
            if(notificationDTO.getRecipientEmail() == null) throw ex;
            return new Notification(notificationDTO.getRecipientEmail(),
                null,
                type,
                cryptoService.encryptDB(notificationDTO.getSubject()),
                null,
                notificationDTO.getContent()
            );
        }
    }
    private String getNotificationMail(Notification notification) {
        return Objects.requireNonNullElseGet(notification.getRecipientMail(),
            () -> cryptoService.decryptDB(notification.getRecipient().getMail())
        );
    }
    private NotificationMailData toMailData(Notification notification) {
        return new NotificationMailData(cryptoService.decryptDB(notification.getSubject()),
            Objects.requireNonNullElseGet(notification.getFullContent(),
                () -> cryptoService.decryptDB(notification.getContent())
            )
        );
    }
    @Override
    @Transactional
    public void sendSummary(Set<EmailCycle> sendCycle) {
        sendCycle.stream()
            .flatMap(notificationRepository::findCycleNotifications)
            .collect(Collectors.groupingBy(this::getNotificationMail))
            .forEach((mail, notifications) -> {
                mailService.sendNotificationSummary(mail,
                    notifications.stream().map(this::toMailData).collect(Collectors.toUnmodifiableList())
                );
                notificationRepository.deleteAllByIdInBatch(notifications.stream()
                    .map(Notification::getId)
                    .collect(Collectors.toUnmodifiableList()));
            });
    }
}
