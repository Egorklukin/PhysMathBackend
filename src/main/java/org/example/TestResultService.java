package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestResultService {

    private final TestResultRepository repository;

    public TestResultEntity saveResult(TestResultDto dto) {
        var entity = new TestResultEntity();
        entity.setLessonId(dto.getLessonId());
        entity.setUserId(dto.getUserId());
        entity.setScore(dto.getScore());
        entity.setTotalQuestions(dto.getTotalQuestions());
        entity.setAnswersJson(dto.getAnswersJson());
        return repository.save(entity);
    }

    public List<TestResultEntity> getResultsByLesson(String lessonId) {
        return repository.findByLessonId(lessonId);
    }

    public Map<String, Object> getLessonStats(String lessonId) {
        var results = repository.findByLessonId(lessonId);

        if (results.isEmpty()) {
            return Map.of(
                    "totalAttempts", 0,
                    "averageScore", 0.0,
                    "passRate", 0.0
            );
        }

        var scores = results.stream()
                .map(TestResultEntity::getScore)
                .collect(Collectors.toList());

        double avgScore = scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        long passCount = results.stream()
                .filter(r -> r.getScore() >= r.getTotalQuestions() * 0.7)
                .count();

        return Map.of(
                "totalAttempts", results.size(),
                "averageScore", Math.round(avgScore * 10.0) / 10.0,
                "passRate", Math.round((double) passCount / results.size() * 100)
        );
    }
}