package com.harish.hk185080.chatterbox.model;

import com.google.firebase.database.ServerValue;

public class User {
    private String userID;
    private String email;
    private String phoneNumber;
    private String profilePictureURL;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String location;
    private String bio;
    private Object createdAt;
    private Object updatedAt;
    private Object lastSeenAt;

    public User() {
    }

    // Private constructor to enforce the use of the Builder
    private User(Builder builder) {
        this.userID = builder.userID;
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
        this.lastSeenAt = builder.lastSeenAt;
    }

    // Getters for each field
    public String getUserID() {
        return userID;
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

    public Object getCreatedAt() {
        return createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public Object getLastSeenAt() {
        return lastSeenAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureURL='" + profilePictureURL + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastSeenAt +
                '}';
    }

    // Builder class
    public static class Builder {
        private String userID;
        private String email;
        private String phoneNumber;
        private String profilePictureURL;
        private String fullName;
        private String dateOfBirth;
        private String gender;
        private String location;
        private String bio;
        private Object createdAt = ServerValue.TIMESTAMP; // Default Firebase timestamp
        private Object updatedAt = ServerValue.TIMESTAMP; // Default Firebase timestamp
        private Object lastSeenAt = ServerValue.TIMESTAMP; // Default Firebase timestamp


        // Constructor with required fields
        public Builder(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }

        // Optional fields with method chaining
        public Builder userID(String userID) {
            this.userID = userID;
            return this;
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

        public Builder createdAt(Object createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Object updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder lastSeenAt(Object lastSeenAt) {
            this.lastSeenAt = lastSeenAt;
            return this;
        }

        // Build method to create an instance of User
        public User build() {
            return new User(this);
        }
    }
}




