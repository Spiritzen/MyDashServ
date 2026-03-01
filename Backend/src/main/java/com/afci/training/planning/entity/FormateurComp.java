package com.afci.training.planning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "formateur_comp")
@IdClass(FormateurCompId.class)
public class FormateurComp {

    // --- Clé composite ---
    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    private Formateur formateur;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    // --- Champs existants ---
    @Column(name = "niveau")          // 1..5 (déjà présent en base)
    private Byte niveau;

    @Column(name = "derniere_maj", nullable = false)
    private LocalDateTime derniereMaj = LocalDateTime.now();

    // --- Nouveaux champs ---
    @Column(name = "title", length = 160)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "experience_years")
    private Byte experienceYears;      // 0..50

    @Column(name = "last_used")
    private LocalDate lastUsed;

    public enum CompetenceStatus { PENDING, VALIDATED, REJECTED }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CompetenceStatus status = CompetenceStatus.PENDING;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    // --- Constructeurs ---
    public FormateurComp() {}

    public FormateurComp(Formateur formateur, Competence competence, Byte niveau) {
        this.formateur = formateur;
        this.competence = competence;
        this.niveau = niveau;
    }

    public FormateurComp(Formateur formateur, Competence competence, Byte niveau,
                         String title, String description, Byte experienceYears,
                         LocalDate lastUsed, CompetenceStatus status, boolean visible) {
        this(formateur, competence, niveau);
        this.title = title;
        this.description = description;
        this.experienceYears = experienceYears;
        this.lastUsed = lastUsed;
        if (status != null) this.status = status;
        this.visible = visible;
    }

    // --- Hooks ---
    @PrePersist
    protected void onCreate() {
        if (derniereMaj == null) derniereMaj = LocalDateTime.now();
        if (status == null) status = CompetenceStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.derniereMaj = LocalDateTime.now();
    }

    // --- Getters / Setters ---
    public Formateur getFormateur() { return formateur; }
    public void setFormateur(Formateur formateur) { this.formateur = formateur; }

    public Competence getCompetence() { return competence; }
    public void setCompetence(Competence competence) { this.competence = competence; }

    public Byte getNiveau() { return niveau; }
    public void setNiveau(Byte niveau) { this.niveau = niveau; }

    public LocalDateTime getDerniereMaj() { return derniereMaj; }
    public void setDerniereMaj(LocalDateTime derniereMaj) { this.derniereMaj = derniereMaj; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Byte getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Byte experienceYears) { this.experienceYears = experienceYears; }

    public LocalDate getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDate lastUsed) { this.lastUsed = lastUsed; }

    public CompetenceStatus getStatus() { return status; }
    public void setStatus(CompetenceStatus status) { this.status = status; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    @Override
    public String toString() {
        return "FormateurComp{formateurId=" + (formateur != null ? formateur.getIdFormateur() : null) +
               ", competenceId=" + (competence != null ? competence.getIdCompetence() : null) +
               ", niveau=" + niveau +
               ", title='" + title + '\'' +
               ", status=" + status +
               ", derniereMaj=" + derniereMaj + "}";
    }
}
