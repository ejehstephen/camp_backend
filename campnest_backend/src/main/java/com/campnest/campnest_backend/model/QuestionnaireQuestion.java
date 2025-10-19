package com.campnest.campnest_backend.model;


import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questionnaire_questions")
public class QuestionnaireQuestion {

    @Id
    private UUID id; // match frontend IDs

    private String question;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> options;

    private String type; // 'single', 'multiple', 'range'

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
