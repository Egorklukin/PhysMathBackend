package org.example;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String lessonId; // Связь с уроком
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String optionsJson; // JSON-массив: ["A", "B", "C", "D"]

    private String correctAnswer;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String explanation;

    private String difficulty; // EASY, MEDIUM, HARD
    private int orderIndex;    // Порядок вопросов в тесте
}