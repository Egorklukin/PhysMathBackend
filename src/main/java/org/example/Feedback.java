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

    private String userName;
    private String userEmail;
    private String requestType;
    private String topic;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String deviceInfo;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}