// src/main/java/com/afci/training/planning/dto/FormationOptionDTO.java
package com.afci.training.planning.dto;

public class FormationOptionDTO {

    private Integer id;
    private String intitule;
    private String modeDefaut;  // "PRESENTIEL" | "DISTANCIEL" | "HYBRIDE"
    private String villeDefaut;
    private String salleDefaut;

    public FormationOptionDTO() {
    }

    public FormationOptionDTO(Integer id, String intitule, String modeDefaut, String villeDefaut, String salleDefaut) {
        this.id = id;
        this.intitule = intitule;
        this.modeDefaut = modeDefaut;
        this.villeDefaut = villeDefaut;
        this.salleDefaut = salleDefaut;
    }

    // Getters
    public Integer getId() { return id; }
    public String getIntitule() { return intitule; }
    public String getModeDefaut() { return modeDefaut; }
    public String getVilleDefaut() { return villeDefaut; }
    public String getSalleDefaut() { return salleDefaut; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public void setModeDefaut(String modeDefaut) { this.modeDefaut = modeDefaut; }
    public void setVilleDefaut(String villeDefaut) { this.villeDefaut = villeDefaut; }
    public void setSalleDefaut(String salleDefaut) { this.salleDefaut = salleDefaut; }
}
