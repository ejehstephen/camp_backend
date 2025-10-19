package com.campnest.campnest_backend.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questionnaire_answers")
public class QuestionnaireAnswer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionnaireQuestion question;

    @ElementCollection
    @CollectionTable(name = "answer_values", joinColumns = @JoinColumn(name = "answer_id"))
    private List<String> answers;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public QuestionnaireQuestion getQuestion() { return question; }
    public void setQuestion(QuestionnaireQuestion question) { this.question = question; }
    public List<String> getAnswers() { return answers; }
    public void setAnswers(List<String> answers) { this.answers = answers; }
}
