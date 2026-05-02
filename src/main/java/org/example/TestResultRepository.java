package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResultEntity, String> {
    List<TestResultEntity> findByLessonId(String lessonId);
    List<TestResultEntity> findByUserId(String userId);
}
