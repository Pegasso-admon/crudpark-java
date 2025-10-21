// model/Membership.java
package com.crudpark.model;

import java.time.LocalDate;

public class Membership {
    private int id;
    private String ownerName;
    private String ownerEmail;
    private String plate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public Membership() {}

    public Membership(int id, String ownerName, String ownerEmail, String plate, 
                      LocalDate startDate, LocalDate endDate, boolean active) {
        this.id = id;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.plate = plate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return active && !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}