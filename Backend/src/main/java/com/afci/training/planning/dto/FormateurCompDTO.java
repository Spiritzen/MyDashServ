package com.afci.training.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FormateurCompDTO {
    private Integer competenceId;
    private String  competenceLabel;
    private Byte    niveau;
    private String  title;
    private String  description;
    private Byte    experienceYears;
    private LocalDate lastUsed;
    private String  status;            // "PENDING"/"VALIDATED"/"REJECTED"
    private boolean visible;
    private LocalDateTime derniereMaj;

    public FormateurCompDTO() {}

    public FormateurCompDTO(Integer competenceId, String competenceLabel, Byte niveau, String title,
                            String description, Byte experienceYears, LocalDate lastUsed,
                            String status, boolean visible, LocalDateTime derniereMaj) {
        this.competenceId = competenceId; this.competenceLabel = competenceLabel; this.niveau = niveau;
        this.title = title; this.description = description; this.experienceYears = experienceYears;
        this.lastUsed = lastUsed; this.status = status; this.visible = visible; this.derniereMaj = derniereMaj;
    }

    public Integer getCompetenceId() { return competenceId; }
    public void setCompetenceId(Integer competenceId) { this.competenceId = competenceId; }
    public String getCompetenceLabel() { return competenceLabel; }
    public void setCompetenceLabel(String competenceLabel) { this.competenceLabel = competenceLabel; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public LocalDateTime getDerniereMaj() { return derniereMaj; }
    public void setDerniereMaj(LocalDateTime derniereMaj) { this.derniereMaj = derniereMaj; }
}
