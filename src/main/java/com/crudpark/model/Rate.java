// model/Rate.java
package com.crudpark.model;

import java.math.BigDecimal;

public class Rate {
    private int id;
    private BigDecimal baseRate; // per hour
    private BigDecimal fractionRate; // per fraction after first hour
    private BigDecimal dailyCap; // maximum charge per day
    private int graceMinutes; // minimum 30 minutes
    private boolean active;

    public Rate() {}

    public Rate(int id, BigDecimal baseRate, BigDecimal fractionRate, 
                BigDecimal dailyCap, int graceMinutes, boolean active) {
        this.id = id;
        this.baseRate = baseRate;
        this.fractionRate = fractionRate;
        this.dailyCap = dailyCap;
        this.graceMinutes = graceMinutes;
        this.active = active;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
    
    public BigDecimal getFractionRate() { return fractionRate; }
    public void setFractionRate(BigDecimal fractionRate) { this.fractionRate = fractionRate; }
    
    public BigDecimal getDailyCap() { return dailyCap; }
    public void setDailyCap(BigDecimal dailyCap) { this.dailyCap = dailyCap; }
    
    public int getGraceMinutes() { return graceMinutes; }
    public void setGraceMinutes(int graceMinutes) { this.graceMinutes = graceMinutes; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}