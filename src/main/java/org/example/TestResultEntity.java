package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "test_results")
public class TestResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String lessonId;
    private String userId; // Для будущей авторизации
    private Integer score;
    private Integer totalQuestions;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String answersJson;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}