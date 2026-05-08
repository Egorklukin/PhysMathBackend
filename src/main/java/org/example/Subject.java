package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    private String id;  // "math", "physics"

    private String title;  // "Математика", "Физика"
    private String icon;   // "ic_math", "ic_physics" - ресурс для иконки
    private String color;  // "#2196F3" - цвет карточки

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Topic> topics;
}
