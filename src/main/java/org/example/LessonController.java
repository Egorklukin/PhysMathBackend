package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LessonController {
    private final ObjectMapper mapper = new ObjectMapper();
    private final LessonService lessonService;
    private final QuestionRepository questionRepository;
    private final GigaChatService gigaChatService; // ✅ Добавьте в конструктор

    // 📥 Получить все уроки (без изменений)
    @GetMapping("/lessons")
    public ResponseEntity<List<Lesson>> getLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    // 📥 Получить урок по ID (без изменений)
    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable String id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/lessons")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.saveLesson(lesson));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable String id,
            @RequestBody Lesson lessonDetails) {

        return lessonService.getLessonById(id)
                .map(existing -> {
                    existing.setTitle(lessonDetails.getTitle());
                    existing.setContentMd(lessonDetails.getContentMd());
                    return ResponseEntity.ok(lessonService.saveLesson(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/lessons/{lessonId}/explain")
    public ResponseEntity<Map<String, String>> getExplanation(
            @PathVariable String lessonId,
            @RequestBody ExplainRequest request) {

        try {
            String explanation = gigaChatService.generateExplanation(
                    request.getQuestionText(),
                    request.getUserAnswer(),
                    request.getCorrectAnswer()
            );
            return ResponseEntity.ok(Map.of("explanation", explanation));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "explanation", "Правильный ответ: \"" + request.getCorrectAnswer() + "\". Попробуйте ещё раз! 💪"
            ));
        }
    }
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        if (lessonService.getLessonById(id).isPresent()) {
            lessonService.deleteLesson(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lessons/{lessonId}/questions")
    public ResponseEntity<List<Question>> getQuestions(
            @PathVariable String lessonId,
            @RequestParam(defaultValue = "false") boolean force) {  // ✅ Параметр в том же методе

        if (!force) {
            List<Question> cachedQuestions = questionRepository.findByLessonIdOrderByOrderIndexAsc(lessonId);
            if (!cachedQuestions.isEmpty()) {
                return ResponseEntity.ok(cachedQuestions);
            }
        } else {
            questionRepository.deleteByLessonId(lessonId);
        }

        try {
            Lesson lesson = lessonService.getLessonById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

            List<Question> generatedQuestions = gigaChatService.generateQuestions(
                    lessonId,
                    lesson.getTitle(),
                    lesson.getContentMd()
            );

            if (generatedQuestions == null || generatedQuestions.isEmpty()) {
                return ResponseEntity.ok(getFallbackQuestions(lessonId));
            }

            questionRepository.saveAll(generatedQuestions);
            return ResponseEntity.ok(generatedQuestions);

        } catch (Exception e) {
            return ResponseEntity.ok(getFallbackQuestions(lessonId));
        }
    }

    // 🔹 Заглушка на случай сбоя ИИ
    // ✅ Исправленный метод — без throws, с try-catch внутри
    private List<Question> getFallbackQuestions(String lessonId) {
        try {
            Question fallback = new Question();
            fallback.setId(lessonId + "_fallback_1");
            fallback.setLessonId(lessonId);
            fallback.setQuestionText("⚠️ Вопросы временно недоступны");
            // ✅ Безопасная сериализация с try-catch
            fallback.setOptionsJson(mapper.writeValueAsString(
                    List.of("Попробовать ещё раз", "Вернуться к уроку", "Пропустить", "Сообщить об ошибке")
            ));
            fallback.setCorrectAnswer("Попробовать ещё раз");
            fallback.setExplanation("Сервер временно не может сгенерировать вопросы. Это не ваша ошибка!");
            fallback.setDifficulty("EASY");
            fallback.setOrderIndex(1);
            return List.of(fallback);
        } catch (JsonProcessingException e) {
            // ✅ Если даже fallback не удалось — возвращаем минимальный вопрос
            Question minimal = new Question();
            minimal.setId(lessonId + "_minimal");
            minimal.setLessonId(lessonId);
            minimal.setQuestionText("Ошибка загрузки вопросов");
            minimal.setOptionsJson("[\"Перезагрузить\"]");
            minimal.setCorrectAnswer("Перезагрузить");
            minimal.setExplanation("Попробуйте обновить страницу");
            minimal.setDifficulty("EASY");
            minimal.setOrderIndex(1);
            return List.of(minimal);
        }
    }
}