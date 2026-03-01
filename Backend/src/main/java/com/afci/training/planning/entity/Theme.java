package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "theme",
       uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_theme")
    private Integer idTheme;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // --- Constructeurs ---
    public Theme() {}

    public Theme(String name) {
        this.name = name;
    }

    public Theme(Integer idTheme, String name, boolean active) {
        this.idTheme = idTheme;
        this.name = name;
        this.active = active;
    }

    // --- Hooks ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        if (this.name != null) this.name = this.name.trim();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        if (this.name != null) this.name = this.name.trim();
    }

    // --- Getters / Setters ---
    public Integer getIdTheme() { return idTheme; }
    public void setIdTheme(Integer idTheme) { this.idTheme = idTheme; }

    public String getName() { return name; }
    public void setName(String name) { this.name = (name != null ? name.trim() : null); }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Theme{id=" + idTheme +
               ", name='" + name + '\'' +
               ", active=" + active + "}";
    }
}
