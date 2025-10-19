package com.campnest.campnest_backend.dto;

import java.util.List;
import java.util.UUID;

public class QuestionnaireQuestionDTO {
    private UUID id;
    private String question;
    private List<String> options;
    private String type;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
