package org.example;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByLessonIdOrderByOrderIndexAsc(String lessonId);

    // ✅ Новый метод для удаления вопросов урока
    @Transactional
    @Modifying
    @Query("DELETE FROM Question q WHERE q.lessonId = :lessonId")
    void deleteByLessonId(@Param("lessonId") String lessonId);
}