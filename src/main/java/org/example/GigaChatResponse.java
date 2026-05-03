package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Игнорировать неизвестные поля
public class GigaChatResponse {
    private List<Choice> choices;
    private String created;
    private String model;
    private String object;
    private Usage usage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)  // ✅ То же для вложенных классов
    public static class Choice {
        private Message message;
        private Integer index;
        private String finish_reason;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Ключевое исправление!
    public static class Message {
        private String content;
        // Поле "role" будет проигнорировано автоматически
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}