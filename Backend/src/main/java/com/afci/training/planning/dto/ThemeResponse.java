package com.afci.training.planning.dto;

import java.time.Instant;

public class ThemeResponse {
    private Integer id;
    private String name;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public ThemeResponse() {}

    public ThemeResponse(Integer id, String name, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id; this.name = name; this.active = active; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
