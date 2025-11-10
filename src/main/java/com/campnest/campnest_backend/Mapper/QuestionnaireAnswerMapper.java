package com.campnest.campnest_backend.Mapper;

import com.campnest.campnest_backend.dto.QuestionnaireAnswerDTO;
import com.campnest.campnest_backend.model.QuestionnaireAnswer;
import com.campnest.campnest_backend.model.QuestionnaireQuestion;
import com.campnest.campnest_backend.model.User;

public class QuestionnaireAnswerMapper {

    // Entity -> DTO
    public static QuestionnaireAnswerDTO toDTO(QuestionnaireAnswer answer) {
        QuestionnaireAnswerDTO dto = new QuestionnaireAnswerDTO();
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setAnswers(answer.getAnswers());
        return dto;
    }
    // DTO -> Entity
    public static QuestionnaireAnswer toEntity(QuestionnaireAnswerDTO dto, User user, QuestionnaireQuestion question) {
        QuestionnaireAnswer answer = new QuestionnaireAnswer();
        answer.setUser(user);
        answer.setQuestion(question);

        // ✅ Save the selected options
        answer.setAnswers(dto.getAnswers());

        // ✅ Fix: set "text" value for the DB column (avoid null)
        if (dto.getAnswers() != null && !dto.getAnswers().isEmpty()) {
            // Join all answers into one string, e.g. "Music, Movies"
            answer.setText(String.join(", ", dto.getAnswers()));
        } else {
            // If user didn’t select anything, prevent null crash
            answer.setText("N/A");
        }

        return answer;
    }

}
