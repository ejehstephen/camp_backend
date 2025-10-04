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

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String name;
    private String school;
    private Integer age;
    private String gender;


    // --- Cascade delete for listings ---
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomListing> listings;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Column(nullable = false)
    private String phoneNumber;

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<RoomListing> getListings() { return listings; }
    public void setListings(List<RoomListing> listings) { this.listings = listings; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}

//538688576271882
//D_o6KNGridJzbJw1wQFgUqaLw1s
//        dkz95najt