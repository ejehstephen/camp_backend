package com.campnest.campnest_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room_listings")
public class RoomListing {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Column(length = 1000)
    private String description;

    private Double price;

    private String location;

    @ElementCollection
    private List<String> images;  // store image URLs

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ElementCollection
    private List<String> amenities;

    @ElementCollection
    private List<String> rules;

    private String genderPreference; // "any", "male", "female"

    private LocalDate availableFrom;

    private Boolean isActive = true;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public String getGenderPreference() {
        return genderPreference;
    }

    public void setGenderPreference(String genderPreference) {
        this.genderPreference = genderPreference;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
