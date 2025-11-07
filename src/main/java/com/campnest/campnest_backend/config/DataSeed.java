package com.campnest.campnest_backend.config;

import com.campnest.campnest_backend.model.QuestionnaireAnswer;
import com.campnest.campnest_backend.model.QuestionnaireQuestion;
import com.campnest.campnest_backend.repository.QuestionnaireAnswerRepository;
import com.campnest.campnest_backend.repository.QuestionnaireQuestionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataSeed {

    private final QuestionnaireQuestionRepository questionRepository;
    private final QuestionnaireAnswerRepository answerRepository;

    public DataSeed(QuestionnaireQuestionRepository questionRepository,
                    QuestionnaireAnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @PostConstruct
    public void seedData() {
        System.out.println("🌍 Checking questionnaire data...");

        long count = questionRepository.count();
        if (count > 0) {
            System.out.println("✅ Questionnaire already seeded (" + count + " questions found). Skipping...");
            return;
        }

        System.out.println("🌱 Seeding roommate questionnaire data...");

        List<QuestionnaireQuestion> questions = List.of(
                createQuestion("9c702e54-3bc4-464e-9774-6d07f83301d7",
                        "Do you currently have an apartment or are you looking to join one?",
                        List.of("I have an apartment", "I’m looking to join one")),

                createQuestion("520f28f5-461b-4a9b-ad2b-ca63a7615fc9",
                        "What gender of roommate would you prefer?",
                        List.of("Male", "Female", "Any")),

                createQuestion("d23962bf-d434-4123-b29c-5b949c06daeb",
                        "What is your preferred budget range (in Naira)?",
                        List.of("50k–100k", "100k–200k", "200k+", "Flexible")),

                createQuestion("493b6223-521f-40e4-90a5-011504702e57",
                        "Are you a student or a working professional?",
                        List.of("Student", "Working Professional")),

                createQuestion("8e200177-6591-49f1-8bf2-5808964ee575",
                        "Do you smoke?",
                        List.of("Yes", "No", "Occasionally")),

                createQuestion("73c136ce-b80e-4fe9-88c4-949a4beeaf9a",
                        "Do you drink alcohol?",
                        List.of("Yes", "No", "Occasionally")),

                createQuestion("a110759a-96dc-489f-928a-2199a8f4aad2",
                        "What are your top interests or hobbies?",
                        List.of("Music", "Movies", "Sports", "Reading", "Gaming", "Cooking")),

                createQuestion("2e802aa8-d396-46b7-98fc-ccb0b850bc5d",
                        "Do you have any allergies or dietary restrictions?",
                        List.of("Yes", "No"))
        );

        questionRepository.saveAll(questions);
        System.out.println("✅ Questionnaire seeding complete!");
    }

    private QuestionnaireQuestion createQuestion(String id, String questionText, List<String> options) {
        QuestionnaireQuestion question = new QuestionnaireQuestion();
        question.setId(UUID.fromString(id));
        question.setQuestion(questionText);
        question.setType("single");

        QuestionnaireQuestion saved = questionRepository.save(question);

        for (String opt : options) {
            QuestionnaireAnswer answer = new QuestionnaireAnswer();
            answer.setText(opt);
            answer.setQuestion(saved);
            answerRepository.save(answer);
        }

        return saved;
    }
}
