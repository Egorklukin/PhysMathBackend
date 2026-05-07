package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    private String id;

    private String lessonId;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private String difficulty;
    private Integer orderIndex;
}