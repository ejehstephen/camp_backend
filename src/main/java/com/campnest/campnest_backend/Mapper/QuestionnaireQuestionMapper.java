package com.campnest.campnest_backend.Mapper;

import com.campnest.campnest_backend.dto.QuestionnaireQuestionDTO;
import com.campnest.campnest_backend.model.QuestionnaireQuestion;

public class QuestionnaireQuestionMapper {
    public static QuestionnaireQuestionDTO toDTO(QuestionnaireQuestion q) {
        QuestionnaireQuestionDTO dto = new QuestionnaireQuestionDTO();
        dto.setId(q.getId());
        dto.setQuestion(q.getQuestion());
        dto.setOptions(q.getOptions());
        dto.setType(q.getType());
        return dto;
    }
}
