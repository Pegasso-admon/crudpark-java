// model/Operator.java
package com.crudpark.model;

public class Operator {
    private int id;
    private String username;
    private String passwordHash; // Store hashed passwords, not plain text
    private String name;
    private String email;
    private boolean active;

    public Operator(int id, String username, String passwordHash, String name, String email, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
        this.active = active;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}