package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Для локальной разработки
public class LessonController {

    private final LessonService lessonService;

    // 📥 Получить все уроки
    @GetMapping("/lessons")
    public ResponseEntity<List<Lesson>> getLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    // 📥 Получить урок по ID
    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable String id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 📤 Добавить новый урок (для админки/тестов)
    @PostMapping("/lessons")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.saveLesson(lesson));
    }

    // ✏️ Обновить урок
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

    // 🗑️ Удалить урок
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        // ✅ Вариант 1: Простой и понятный (рекомендуется)
        if (lessonService.getLessonById(id).isPresent()) {
            lessonService.deleteLesson(id);
            return ResponseEntity.ok().build(); // или ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build();
    }
    private final QuestionRepository questionRepository; // Добавьте в конструктор

    @GetMapping("/lessons/{lessonId}/questions")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable String lessonId) {
        return ResponseEntity.ok(questionRepository.findByLessonIdOrderByOrderIndexAsc(lessonId));
    }
}