package com.project.back_end.DTO;

public class Login {

    // The unique identifier of the user:
    // - Email for Doctor/Patient
    // - Username for Admin
    private String identifier;

    // The password provided during login
    private String password;

    // Default constructor (required for @RequestBody deserialization)
    public Login() {
    }

    // Getter for identifier
    public String getIdentifier() {
        return identifier;
    }

    // Setter for identifier
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}