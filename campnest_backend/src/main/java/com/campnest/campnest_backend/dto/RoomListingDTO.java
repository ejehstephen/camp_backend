package com.campnest.campnest_backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RoomListingDTO {

    private UUID id;
    private String title;
    private String description;
    private Double price;
    private String location;
    private List<String> images;
    private List<String> mediaUrls;
    private List<String> amenities;
    private List<String> rules;
    private String genderPreference;
    private LocalDate availableFrom;
    private Boolean isActive;
    private UUID ownerId; // only expose owner id, not the whole User
    private String ownerPhone;
    private String whatsappLink;

    // --- Getters and Setters ---
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

    public UUID getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }


    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getWhatsappLink() { return whatsappLink; }
    public void setWhatsappLink(String whatsappLink) { this.whatsappLink = whatsappLink; }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
