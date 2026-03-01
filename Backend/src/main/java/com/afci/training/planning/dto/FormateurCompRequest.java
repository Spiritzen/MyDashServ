package com.afci.training.planning.dto;

import java.time.LocalDate;

public class FormateurCompRequest {

    private Integer competenceId;
    private Byte niveau;            // 1..5
    private String title;
    private String description;
    private Byte experienceYears;   // 0..50
    private LocalDate lastUsed;
    private Boolean visible;        // défaut true côté entity/service

    public FormateurCompRequest() {}

    public FormateurCompRequest(Integer competenceId, Byte niveau, String title, String description,
                                Byte experienceYears, LocalDate lastUsed, Boolean visible) {
        this.competenceId = competenceId;
        this.niveau = niveau;
        this.title = title;
        this.description = description;
        this.experienceYears = experienceYears;
        this.lastUsed = lastUsed;
        this.visible = visible;
    }

    public Integer getCompetenceId() { return competenceId; }
    public void setCompetenceId(Integer competenceId) { this.competenceId = competenceId; }
    public Byte getNiveau() { return niveau; }
    public void setNiveau(Byte niveau) { this.niveau = niveau; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Byte getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Byte experienceYears) { this.experienceYears = experienceYears; }
    public LocalDate getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDate lastUsed) { this.lastUsed = lastUsed; }
    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
}
