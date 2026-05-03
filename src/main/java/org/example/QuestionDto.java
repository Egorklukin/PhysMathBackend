package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class QuestionDto {
    private String questionText;
    private java.util.List<String> options;
    private String correctAnswer;
    private String explanation;
}