package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExplainRequest {
    private String questionText;
    private String userAnswer;
    private String correctAnswer;
}