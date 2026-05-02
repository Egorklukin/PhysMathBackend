package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    // Возвращает вопросы урока, отсортированные по порядку (возрастание сложности)
    List<Question> findByLessonIdOrderByOrderIndexAsc(String lessonId);
}