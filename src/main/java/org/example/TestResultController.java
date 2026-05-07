package org.example;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestResultController {

    private final TestResultService resultService;

    @PostMapping("/results")
    public ResponseEntity<TestResultEntity> saveResult(@RequestBody TestResultDto dto) {
        return ResponseEntity.ok(resultService.saveResult(dto));
    }

    @GetMapping("/lessons/{lessonId}/results")
    public ResponseEntity<List<TestResultEntity>> getResultsByLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(resultService.getResultsByLesson(lessonId));
    }

    @GetMapping("/lessons/{lessonId}/stats")
    public ResponseEntity<Map<String, Object>> getLessonStats(@PathVariable String lessonId) {
        return ResponseEntity.ok(resultService.getLessonStats(lessonId));
    }
}

@Data
@NoArgsConstructor
class TestResultDto {
    private String lessonId;
    private String userId;
    private Integer score;
    private Integer totalQuestions;
    private String answersJson;
}