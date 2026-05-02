// src/main/java/com/physmath/server/entity/Feedback.java
package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userName;        // Опционально: имя пользователя
    private String userEmail;       // Опционально: для связи
    private String requestType;     // "lesson" или "test"
    private String topic;           // Тема: "Квантовая физика", "Интегралы" и т.д.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;     // Подробное описание пожелания

    private String deviceInfo;      // Модель телефона, версия ОС (для аналитики)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}