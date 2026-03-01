// src/main/java/com/afci/training/planning/dto/FormationDTO.java
package com.afci.training.planning.dto;

public class FormationDTO {
    private Integer id;
    private String intitule;
    private String objectifs;
    private Integer dureeHeures;
    private Integer nbParticipantsMax;
    private String format;
    private String lieu;
    private String theme;
    private Boolean actif;

    public FormationDTO() {}

    public FormationDTO(Integer id, String intitule, String objectifs,
                        Integer dureeHeures, Integer nbParticipantsMax,
                        String format, String lieu, String theme, Boolean actif) {
        this.id = id;
        this.intitule = intitule;
        this.objectifs = objectifs;
        this.dureeHeures = dureeHeures;
        this.nbParticipantsMax = nbParticipantsMax;
        this.format = format;
        this.lieu = lieu;
        this.theme = theme;
        this.actif = actif;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
