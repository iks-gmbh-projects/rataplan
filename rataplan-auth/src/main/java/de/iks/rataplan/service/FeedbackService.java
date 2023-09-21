package de.iks.rataplan.service;

import de.iks.rataplan.dto.FeedbackDTO;

public interface FeedbackService {
    void acceptFeedback(FeedbackDTO feedback);
    void sendFeedback();
}
