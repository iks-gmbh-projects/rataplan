package de.iks.rataplan.service;

import de.iks.rataplan.domain.Feedback;
import de.iks.rataplan.dto.FeedbackDTO;
import de.iks.rataplan.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository repository;
    private final CryptoService cryptoService;
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
        // TODO
    }
}
