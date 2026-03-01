// src/main/java/com/afci/training/planning/dto/FormationUpdateDTO.java
package com.afci.training.planning.dto;

import jakarta.validation.constraints.*;

public class FormationUpdateDTO {
    @NotBlank @Size(max = 150)
    private String intitule;

    @Size(max = 500)
    private String objectifs;

    @Min(1) @Max(1000)
    private Integer dureeHeures;

    @Min(1) @Max(1000)
    private Integer nbParticipantsMax;

    @Size(max = 30)
    private String format;

    @Size(max = 100)
    private String lieu;

    @Size(max = 100)
    private String theme;

    private Boolean actif;

    public FormationUpdateDTO() {}

    // Getters / Setters
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }
    public Integer getDureeHeures() { return dureeHeures; }
    public void setDureeHeures(Integer dureeHeures) { this.dureeHeures = dureeHeures; }
    public Integer getNbParticipantsMax() { return nbParticipantsMax; }
    public void setNbParticipantsMax(Integer nbParticipantsMax) { this.nbParticipantsMax = nbParticipantsMax; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
