package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    private String id;  // "algebra", "mechanics"

    private String title;  // "Алгебра", "Механика"
    private String subjectId;  // Ссылка на Subject.id

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}