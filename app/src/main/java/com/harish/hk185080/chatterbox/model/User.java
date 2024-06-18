package com.harish.hk185080.chatterbox.model;

import java.time.Instant;

public class User {
    private String userID;
    private String username;
    private String email;
    private String phoneNumber;
    private String profilePictureURL;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String location;
    private String bio;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;

    private User(Builder builder) {
        this.userID = builder.userID;
        this.username = builder.username;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.profilePictureURL = builder.profilePictureURL;
        this.fullName = builder.fullName;
        this.dateOfBirth = builder.dateOfBirth;
        this.gender = builder.gender;
        this.location = builder.location;
        this.bio = builder.bio;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.lastLogin = builder.lastLogin;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public static class Builder {
        private String userID;
        private String username;
        private String passwordHash;
        private String email;
        private String phoneNumber;
        private String profilePictureURL;
        private String fullName;
        private String dateOfBirth;
        private String gender;
        private String location;
        private String bio;
        private Instant createdAt = Instant.now();
        private Instant updatedAt = Instant.now();
        private Instant lastLogin;

        public Builder(String username, String passwordHash, String email) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.email = email;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder profilePictureURL(String profilePictureURL) {
            this.profilePictureURL = profilePictureURL;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder dateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder lastLogin(Instant lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureURL='" + profilePictureURL + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                '}';
    }

    // Getters for each field can be added here as needed
}



