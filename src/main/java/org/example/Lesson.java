package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    private String id;
    private String title;
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