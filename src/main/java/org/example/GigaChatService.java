package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class GigaChatService {

    private WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gigachat.auth-url:https://ngw.devices.sberbank.ru:9443/api/v2/oauth}")
    private String authUrl;

    @Value("${gigachat.client-id:}")
    private String clientId;

    @Value("${gigachat.client-secret:}")
    private String clientSecret;

    @Value("${gigachat.api-url:https://gigachat.devices.sberbank.ru/api/v1/chat/completions}")
    private String apiUrl;

    private volatile String accessToken;
    private volatile long tokenExpiry;

    // ✅ Инициализация WebClient после инъекции свойств
    @PostConstruct
    public void init() {
        try {
            var sslContext = io.netty.handler.ssl.SslContextBuilder.forClient().build();

            this.webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(
                            HttpClient.create().secure(ssl -> ssl.sslContext(sslContext))
                    ))
                    .build();

            log.info("✅ GigaChatService initialized, authUrl={}", authUrl);

        } catch (Exception e) {
            log.error("❌ Failed to initialize GigaChatService", e);
            throw new RuntimeException("SSL setup failed", e);
        }
    }

    // 🔑 Получение токена
    private synchronized String getToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry - 60000) {
            return accessToken;
        }

        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            log.error("❌ GigaChat credentials not configured");
            throw new RuntimeException("Missing GigaChat credentials");
        }

        try {
            String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            String response = webClient.post()
                    .uri(authUrl)
                    .header("Authorization", "Basic " + credentials)
                    .header("RqUID", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue("scope=GIGACHAT_API_PERS")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = mapper.readTree(response);
            this.accessToken = node.get("access_token").asText();
            this.tokenExpiry = System.currentTimeMillis() + (node.get("expires_at").asLong() * 1000);
            log.debug("✅ Token received, expires at: {}", tokenExpiry);
            return accessToken;
        } catch (Exception e) {
            log.error("❌ Failed to get GigaChat token", e);
            throw new RuntimeException("Auth failed", e);
        }
    }

    // 📝 Генерация вопросов — ИСПРАВЛЕННАЯ ВЕРСИЯ
    public List<Question> generateQuestions(String lessonId, String title, String content) {
        try {
            // ✅ Короткий и чёткий промпт (чтобы не превысить лимит токенов)
            String systemPrompt = """
            Ты эксперт-преподаватель. Сгенерируй 5 вопросов по теме "%s".
            Материал: %s
            
            ТРЕБОВАНИЯ:
            1. БЕЗ символов $ и \\ (пиши формулы текстом: x², a/b, √x)
            2. БЕЗ LaTeX, markdown и лишнего форматирования
            3. Верни ТОЛЬКО валидный JSON массив объектов:
            [{"questionText":"...","options":["A","B","C","D"],"correctAnswer":"...","explanation":"..."}]
            """.formatted(title, content);

            String token = getToken();

            // ✅ Правильное формирование JSON через Jackson (автоматическое экранирование!)
            Map<String, Object> message = Map.of("role", "user", "content", systemPrompt);
            Map<String, Object> requestBody = Map.of(
                    "model", "GigaChat",
                    "messages", List.of(message),
                    "temperature", 0.3
            );

            // Jackson сам экранирует все спецсимволы в systemPrompt
            String jsonRequestBody = mapper.writeValueAsString(requestBody);

            String responseBody = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonRequestBody)  // ✅ Передаём уже готовый JSON-строку
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseBody == null || responseBody.isEmpty()) {
                throw new RuntimeException("Empty response from GigaChat");
            }

            // Логируем ответ для отладки (обрезаем до 500 символов)
            log.debug("🤖 GigaChat raw response: {}",
                    responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody);

            JsonNode root = mapper.readTree(responseBody);

            // Проверка на наличие ошибок в ответе API
            if (root.has("error")) {
                String errorMsg = root.path("error").path("message").asText("Unknown error");
                log.error("❌ GigaChat API error: {}", errorMsg);
                throw new RuntimeException("GigaChat API error: " + errorMsg);
            }

            String contentRaw = root.path("choices").get(0).path("message").path("content").asText();

            String jsonText = contentRaw.replaceAll("```json\\s*|\\s*```|```\\s*|\\s*```", "").trim();

            if (!jsonText.startsWith("[") && !jsonText.startsWith("{")) {
                log.warn("⚠️ GigaChat returned non-JSON: {}", jsonText.substring(0, Math.min(100, jsonText.length())));
                throw new RuntimeException("Invalid JSON format from GigaChat");
            }

            QuestionDto[] dtos;
            try {
                if (jsonText.startsWith("{")) {
                    jsonText = "[" + jsonText + "]";
                }
                dtos = mapper.readValue(jsonText, QuestionDto[].class);
            } catch (JsonProcessingException e) {
                log.error("❌ Failed to parse GigaChat JSON: {}", jsonText, e);
                throw new RuntimeException("JSON parse failed", e);
            }

            return java.util.stream.IntStream.range(0, dtos.length)
                    .mapToObj(i -> {
                        try {
                            Question q = new Question();
                            q.setId(lessonId + "_q_" + UUID.randomUUID().toString().substring(0, 8));
                            q.setLessonId(lessonId);
                            q.setQuestionText(dtos[i].getQuestionText());
                            if (dtos[i].getOptions() != null && !dtos[i].getOptions().isEmpty()) {
                                q.setOptionsJson(mapper.writeValueAsString(dtos[i].getOptions()));
                            } else {
                                q.setOptionsJson("[]");
                            }
                            q.setCorrectAnswer(dtos[i].getCorrectAnswer());
                            q.setExplanation(dtos[i].getExplanation());
                            q.setDifficulty("MEDIUM");
                            q.setOrderIndex(i + 1);
                            return q;
                        } catch (JsonProcessingException e) {
                            log.error("❌ Failed to serialize options for question {}", i, e);
                            return null;
                        }
                    })
                    .filter(q -> q != null)
                    .toList();

        } catch (Exception e) {
            log.error("❌ GigaChat generation failed for lesson {}: {}", lessonId, e.getMessage());
            return List.of();
        }
    }

    // 💡 Генерация пояснения — БЕЗОПАСНАЯ ВЕРСИЯ
    public String generateExplanation(String question, String userAnswer, String correctAnswer) {
        try {
            String prompt = """
            Пользователь ответил неправильно. Дай пояснение (1-2 предложения).
            Вопрос: %s
            Правильный ответ: %s
            Ответ пользователя: %s
            
            ⚠️ НЕ используй $ и \\ в тексте. Пиши формулы просто: x², a/b.
            Верни ТОЛЬКО текст.
            """.formatted(question, correctAnswer, userAnswer);

            String token = getToken();

            GigaChatRequest request = new GigaChatRequest(
                    "GigaChat",
                    List.of(new GigaChatRequest.Message("user", prompt)),
                    0.2
            );

            String requestBody = mapper.writeValueAsString(request);

            String responseBody = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response ->
                            response.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("❌ Explanation API Error {}: {}", response.statusCode(), body);
                                        return Mono.error(new RuntimeException("API Error: " + body));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            if (responseBody == null || responseBody.isEmpty()) {
                return "Правильный ответ: \"" + correctAnswer + "\". Попробуйте ещё раз! 💪";
            }

            GigaChatResponse response = mapper.readValue(responseBody, GigaChatResponse.class);
            return response.getChoices().get(0).getMessage().getContent().trim();

        } catch (Exception e) {
            log.error("❌ Explanation generation failed: {}", e.getMessage());
            return "Правильный ответ: \"" + correctAnswer + "\". Попробуйте ещё раз! 💪";
        }
    }
}

