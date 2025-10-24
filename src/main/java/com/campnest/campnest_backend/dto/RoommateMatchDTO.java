package com.campnest.campnest_backend.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoommateMatchDTO {

    private UUID id;
    private String name;
    private String profileImage;
    private Integer age;
    private String school;
    private String gender;
    private Double budget;
    private Integer compatibilityScore;
    private List<String> commonInterests;
    private Map<String, String> preferences;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public Integer getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Integer compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    public List<String> getCommonInterests() { return commonInterests; }
    public void setCommonInterests(List<String> commonInterests) { this.commonInterests = commonInterests; }
    public Map<String, String> getPreferences() { return preferences; }
    public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }
}
