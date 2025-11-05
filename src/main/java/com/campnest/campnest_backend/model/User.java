package com.campnest.campnest_backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    private String school;
    private Integer age;
    private String gender;

    // --- Cascade delete for listings ---
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomListing> listings;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_image")
    private String profileImage;

    private boolean enabled = false;

    // --- Hooks to handle normalization and timestamps ---

    @PrePersist
    private void prePersist() {
        if (this.profileImage == null || this.profileImage.isEmpty()) {
            this.profileImage = "https://example.com/default-profile.png";
        }
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = Instant.now();
    }
    private void normalizeFields() {
        if (this.gender != null) {
            this.gender = this.gender.trim().toLowerCase();
        }
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
        if (this.name != null) {
            this.name = this.name.trim();
        }
        if (this.school != null) {
            this.school = this.school.trim();
        }

        // Update timestamp
        this.updatedAt = Instant.now();
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<RoomListing> getListings() { return listings; }
    public void setListings(List<RoomListing> listings) { this.listings = listings; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
