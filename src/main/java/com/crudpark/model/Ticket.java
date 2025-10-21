// model/Ticket.java
package com.crudpark.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private String plate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String ticketType; // "MONTHLY" or "GUEST"
    private int operatorId;
    private String operatorName;
    private boolean active;

    public Ticket() {
    }

    public Ticket(int id, String plate, LocalDateTime entryTime, LocalDateTime exitTime,
            String ticketType, int operatorId, String operatorName, boolean active) {
        this.id = id;
        this.plate = plate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.ticketType = ticketType;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.active = active;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}