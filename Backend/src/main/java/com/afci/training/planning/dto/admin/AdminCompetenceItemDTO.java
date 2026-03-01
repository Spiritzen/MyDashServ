// src/main/java/com/afci/training/planning/dto/admin/AdminCompetenceItemDTO.java
package com.afci.training.planning.dto.admin;

import java.time.LocalDate;
import java.util.List;

public class AdminCompetenceItemDTO {
    private Integer idCompetence;
    private String label;           // competence.label
    private Integer niveau;         // 1..5 (ou null)
    private String title;           // fc.title
    private String description;     // fc.description
    private Integer experienceYears;// fc.experienceYears
    private LocalDate lastUsed;     // fc.lastUsed
    private String status;          // PENDING/REJECTED/VALIDATED
    private boolean visible;        // fc.visible
    private List<AdminEvidenceDocDTO> docs;

    public AdminCompetenceItemDTO() {}
    public AdminCompetenceItemDTO(Integer idCompetence, String label, Integer niveau,
                                  String title, String description, Integer experienceYears,
                                  LocalDate lastUsed, String status, boolean visible,
                                  List<AdminEvidenceDocDTO> docs) {
        this.idCompetence = idCompetence; this.label = label; this.niveau = niveau;
        this.title = title; this.description = description; this.experienceYears = experienceYears;
        this.lastUsed = lastUsed; this.status = status; this.visible = visible; this.docs = docs;
    }
    public Integer getIdCompetence() { return idCompetence; }
    public void setIdCompetence(Integer idCompetence) { this.idCompetence = idCompetence; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public LocalDate getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDate lastUsed) { this.lastUsed = lastUsed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public List<AdminEvidenceDocDTO> getDocs() { return docs; }
    public void setDocs(List<AdminEvidenceDocDTO> docs) { this.docs = docs; }
}
