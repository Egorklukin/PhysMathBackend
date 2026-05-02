package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;

    public Feedback saveFeedback(Feedback feedback) {
        // Простая валидация
        if (feedback.getTopic() == null || feedback.getTopic().isBlank()) {
            throw new IllegalArgumentException("Тема пожелания не может быть пустой");
        }
        return repository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return repository.findAll();
    }

    public void deleteFeedback(String id) {
        repository.deleteById(id);
    }
}