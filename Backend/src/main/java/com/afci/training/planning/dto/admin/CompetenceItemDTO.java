package com.afci.training.planning.dto.admin;

public class CompetenceItemDTO {
    private Integer idCompetence;
    private String label;     // "nom" / "libelle" / "intitule" mappé côté service
    private Integer niveau;   // niveau numérique (Integer)

    public CompetenceItemDTO() {}

    public CompetenceItemDTO(Integer idCompetence, String label, Integer niveau) {
        this.idCompetence = idCompetence;
        this.label = label;
        this.niveau = niveau;
    }

    public Integer getIdCompetence() { return idCompetence; }
    public void setIdCompetence(Integer idCompetence) { this.idCompetence = idCompetence; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
}
