package com.campnest.campnest_backend.dto;

import java.util.List;
import java.util.UUID;

public class QuestionnaireAnswerDTO {

    private UUID questionId;
    private List<String> answers;

    // Getters & Setters
    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }
    public List<String> getAnswers() { return answers; }
    public void setAnswers(List<String> answers) { this.answers = answers; }
}
