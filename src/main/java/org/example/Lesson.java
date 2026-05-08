package org.example;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    private String id;
    private String title;
    private String topicId;
    private String subjectId;
    @Column(columnDefinition = "TEXT")
    private String contentMd;
    private boolean isDownloaded;
    private Long downloadedAt;
    private String subject;
    private String category;
    private String difficulty;
    private Integer estimatedMinutes;
    private Integer orderIndex;
}