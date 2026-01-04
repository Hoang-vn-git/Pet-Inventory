package com.example.pet_inventory.models;

/**
 * Represents a user/employee in the system.
 */
public class User {

    private String userName;  // Employee username
    private int userId;       // Unique employee ID

    // ---------------- Constructor ----------------
    public User(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
    }

    // ---------------- Getters & Setters ----------------
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // ---------------- Utility ----------------
    @Override
    public String toString() {
        return "Employee: " + userName + "\nID: " + userId;
    }
}