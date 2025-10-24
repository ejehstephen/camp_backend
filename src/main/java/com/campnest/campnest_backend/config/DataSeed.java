package com.campnest.campnest_backend.config;



import com.campnest.campnest_backend.model.QuestionnaireQuestion;
import com.campnest.campnest_backend.repository.QuestionnaireQuestionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component

public class DataSeed {

    private final QuestionnaireQuestionRepository questionRepository;

    public DataSeed(QuestionnaireQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    @PostConstruct
    public void seedData() {
        if (questionRepository.count() > 0) {
            System.out.println("✅ Questionnaire already seeded, skipping...");
            return;
        }

        System.out.println("🌱 Seeding roommate questionnaire data...");

        List<QuestionnaireQuestion> questions = List.of(
                createQuestion("9c702e54-3bc4-464e-9774-6d07f83301d7",
                        "Do you currently have an apartment or are you looking to join one?"),

                createQuestion("0e34a447-836c-4046-8b93-62eaf37dd85a",
                        "What is your gender?"),

                createQuestion("520f28f5-461b-4a9b-ad2b-ca63a7615fc9",
                        "What gender of roommate would you prefer?"),

                createQuestion("d23962bf-d434-4123-b29c-5b949c06daeb",
                        "What is your preferred budget range (in ₦)?"),

                createQuestion("493b6223-521f-40e4-90a5-011504702e57",
                        "Are you a student or a working professional?"),

                createQuestion("8e200177-6591-49f1-8bf2-5808964ee575",
                        "Do you smoke?"),

                createQuestion("73c136ce-b80e-4fe9-88c4-949a4beeaf9a",
                        "Do you drink alcohol?"),

                createQuestion("a110759a-96dc-489f-928a-2199a8f4aad2",
                        "What are your top interests/hobbies?"),

                createQuestion("2e802aa8-d396-46b7-98fc-ccb0b850bc5d",
                        "Do you have any allergies or dietary restrictions?")
        );

        questionRepository.saveAll(questions);

        System.out.println("✅ Questionnaire seeding complete!");
    }

    private QuestionnaireQuestion createQuestion(String id, String questionText) {
        QuestionnaireQuestion q = new QuestionnaireQuestion();
        q.setId(UUID.fromString(id));
        q.setQuestion(questionText);
        q.setType("single"); // or "multiple" if you later support multi-select
        return q;
    }
}
