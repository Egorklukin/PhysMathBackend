package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    private final LessonRepository lessonRepo;
    private final QuestionRepository questionRepo;

    @Autowired
    public DataSeeder(LessonRepository lessonRepo, QuestionRepository questionRepo) {
        this.lessonRepo = lessonRepo;
        this.questionRepo = questionRepo;
    }

    @Override
    public void run(String... args) {
        if (lessonRepo.count() > 0) return; // Пропускаем, если данные уже есть

        // 📘 Урок 1: Математика (Лёгкий → Средний)
        Lesson math1 = new Lesson();
        math1.setId("math_basics_01");
        math1.setTitle("Математика: Основы алгебры");
        math1.setContentMd("# Основы алгебры\n\nАлгебра изучает операции над переменными и уравнения.\n\n## Линейные уравнения\n`ax + b = 0` → `x = -b/a`\n\n## Квадратные уравнения\n`ax² + bx + c = 0`. Дискриминант: `D = b² - 4ac`.\n\n## Теорема Виета\nДля `x² + px + q = 0`: `x₁ + x₂ = -p`, `x₁ · x₂ = q`.");
        lessonRepo.save(math1);

        Question mathQ1 = new Question();
        mathQ1.setLessonId(math1.getId());
        mathQ1.setQuestionText("Уравнение 2x + 4 = 10. Чему равен x?");
        mathQ1.setOptionsJson("[\"2\", \"3\", \"4\", \"5\"]");
        mathQ1.setCorrectAnswer("3");
        mathQ1.setExplanation("2x = 6 → x = 3");
        mathQ1.setDifficulty("EASY");
        mathQ1.setOrderIndex(1);

        Question mathQ2 = new Question();
        mathQ2.setLessonId(math1.getId());
        mathQ2.setQuestionText("Что такое дискриминант квадратного уравнения?");
        mathQ2.setOptionsJson("[\"b²-4ac\", \"b²+4ac\", \"4ac-b²\", \"2a\"]");
        mathQ2.setCorrectAnswer("b²-4ac");
        mathQ2.setExplanation("Определяет количество действительных корней.");
        mathQ2.setDifficulty("EASY");
        mathQ2.setOrderIndex(2);

        Question mathQ3 = new Question();
        mathQ3.setLessonId(math1.getId());
        mathQ3.setQuestionText("По теореме Виета для x²-5x+6=0 сумма корней равна:");
        mathQ3.setOptionsJson("[\"5\", \"-5\", \"6\", \"-6\"]");
        mathQ3.setCorrectAnswer("5");
        mathQ3.setExplanation("x₁+x₂ = -b/a = 5");
        mathQ3.setDifficulty("MEDIUM");
        mathQ3.setOrderIndex(3);

        questionRepo.saveAll(Arrays.asList(mathQ1, mathQ2, mathQ3));

        // ⚛️ Урок 2: Физика (Средний → Сложный)
        Lesson phys1 = new Lesson();
        phys1.setId("physics_newton_01");
        phys1.setTitle("Физика: Законы Ньютона");
        phys1.setContentMd("# Законы Ньютона\n\n## Первый закон (Инерция)\nТело сохраняет состояние покоя или равномерного движения, если сумма сил равна нулю.\n\n## Второй закон\n`F = m · a`. Ускорение прямо пропорционально силе.\n\n## Третий закон\n`F₁₂ = -F₂₁`. Силы взаимодействия равны и противоположны.");
        lessonRepo.save(phys1);

        Question physQ1 = new Question();
        physQ1.setLessonId(phys1.getId());
        physQ1.setQuestionText("Первый закон Ньютона также называют:");
        physQ1.setOptionsJson("[\"Закон инерции\", \"Закон гравитации\", \"Закон трения\", \"Закон сохранения энергии\"]");
        physQ1.setCorrectAnswer("Закон инерции");
        physQ1.setExplanation("Тело сохраняет состояние покоя или равномерного движения.");
        physQ1.setDifficulty("EASY");
        physQ1.setOrderIndex(1);

        Question physQ2 = new Question();
        physQ2.setLessonId(phys1.getId());
        physQ2.setQuestionText("Тело массой 5 кг движется с ускорением 2 м/с². Сила равна:");
        physQ2.setOptionsJson("[\"10 Н\", \"2.5 Н\", \"7 Н\", \"0.4 Н\"]");
        physQ2.setCorrectAnswer("10 Н");
        physQ2.setExplanation("F = m·a = 5·2 = 10 Н");
        physQ2.setDifficulty("MEDIUM");
        physQ2.setOrderIndex(2);

        Question physQ3 = new Question();
        physQ3.setLessonId(phys1.getId());
        physQ3.setQuestionText("Ракета выбрасывает газы назад. Почему она летит вперёд?");
        physQ3.setOptionsJson("[\"Из-за 3-го закона Ньютона\", \"Из-за гравитации\", \"Из-за трения\", \"Из-за магнитного поля\"]");
        physQ3.setCorrectAnswer("Из-за 3-го закона Ньютона");
        physQ3.setExplanation("Сила действия равна силе противодействия.");
        physQ3.setDifficulty("HARD");
        physQ3.setOrderIndex(3);

        questionRepo.saveAll(Arrays.asList(physQ1, physQ2, physQ3));

        // 🚀 Урок 3: Продвинутый (Математика + Физика)
        Lesson adv1 = new Lesson();
        adv1.setId("math_phys_advanced_01");
        adv1.setTitle("Производные и Кинематика");
        adv1.setContentMd("# Производные и Кинематика\n\n## Производная\n`f'(x)` показывает скорость изменения. Для `f(x) = xⁿ`: `f'(x) = n·xⁿ⁻¹`.\n\n## Кинематика\n- Скорость: `v(t) = x'(t)`\n- Ускорение: `a(t) = v'(t)`\n\n## Интеграл\nПеремещение: `Δx = ∫ v(t) dt`");
        lessonRepo.save(adv1);

        Question advQ1 = new Question();
        advQ1.setLessonId(adv1.getId());
        advQ1.setQuestionText("Производная функции f(x) = x² равна:");
        advQ1.setOptionsJson("[\"2x\", \"x\", \"x²/2\", \"1\"]");
        advQ1.setCorrectAnswer("2x");
        advQ1.setExplanation("Правило степенной функции: (xⁿ)' = n·xⁿ⁻¹");
        advQ1.setDifficulty("MEDIUM");
        advQ1.setOrderIndex(1);

        Question advQ2 = new Question();
        advQ2.setLessonId(adv1.getId());
        advQ2.setQuestionText("Координата тела x(t) = 3t². Скорость в момент t=2 с:");
        advQ2.setOptionsJson("[\"12 м/с\", \"6 м/с\", \"3 м/с\", \"9 м/с\"]");
        advQ2.setCorrectAnswer("12 м/с");
        advQ2.setExplanation("v(t) = x'(t) = 6t → v(2) = 12");
        advQ2.setDifficulty("HARD");
        advQ2.setOrderIndex(2);

        Question advQ3 = new Question();
        advQ3.setLessonId(adv1.getId());
        advQ3.setQuestionText("Если v(t) = 4t + 1, то ускорение равно:");
        advQ3.setOptionsJson("[\"4 м/с²\", \"1 м/с²\", \"4t м/с²\", \"5 м/с²\"]");
        advQ3.setCorrectAnswer("4 м/с²");
        advQ3.setExplanation("a(t) = v'(t) = 4. Ускорение постоянно.");
        advQ3.setDifficulty("HARD");
        advQ3.setOrderIndex(3);

        questionRepo.saveAll(Arrays.asList(advQ1, advQ2, advQ3));

        System.out.println("✅ Добавлено " + lessonRepo.count() + " уроков и " + questionRepo.count() + " вопросов!");
    }
}