package com.campnest.campnest_backend.repository;

import com.campnest.campnest_backend.model.QuestionnaireQuestion;
import com.campnest.campnest_backend.model.QuestionnaireAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionnaireQuestionRepository extends JpaRepository<QuestionnaireQuestion, UUID> { }

