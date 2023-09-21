package de.iks.rataplan.service;

import de.iks.rataplan.domain.Feedback;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.dto.FeedbackDTO;
import de.iks.rataplan.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository repository;
    private final CryptoService cryptoService;
    private final MailService mailService;
    @Override
    public void acceptFeedback(FeedbackDTO feedback) {
        feedback.assertValid();
        Feedback entity = new Feedback(
            cryptoService.decryptDBRaw(feedback.getTitle()),
            cryptoService.decryptDBRaw(feedback.getText()),
            feedback.getRating(),
            feedback.getCategory()
        );
        repository.saveAndFlush(entity);
    }
    @Override
    public void sendFeedback() {
        List<Feedback> feedback = repository.findBySent(false)
            .collect(Collectors.toUnmodifiableList());
        if(feedback.isEmpty()) return;
        EnumMap<FeedbackCategory, List<FeedbackDTO>> dtos = feedback.stream()
            .map(f -> new FeedbackDTO(
                cryptoService.decryptDBRaw(f.getTitle()),
                cryptoService.decryptDBRaw(f.getText()),
                f.getRating(),
                f.getCategory()
            ))
            .collect(Collectors.groupingBy(
                FeedbackDTO::getCategory,
                () -> new EnumMap<>(FeedbackCategory.class),
                Collectors.toList()
            ));
        mailService.sendFeedbackReport(dtos);
        feedback.forEach(f -> f.setSent(true));
        repository.saveAllAndFlush(feedback);
    }
}
