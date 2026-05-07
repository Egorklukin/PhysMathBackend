package org.example;


@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class QuestionDto {
    private String questionText;
    private java.util.List<String> options;
    private String correctAnswer;
    private String explanation;
}