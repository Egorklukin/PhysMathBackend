package org.example;

import lombok.RequiredArgsConstructor;
import org.example.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository repository;

    public List<Lesson> getAllLessons() {
        return repository.findAll();
    }

    public Optional<Lesson> getLessonById(String id) {
        return repository.findById(id);
    }

    public Lesson saveLesson(Lesson lesson) {
        return repository.save(lesson);
    }

    public void deleteLesson(String id) {
        repository.deleteById(id);
    }
}