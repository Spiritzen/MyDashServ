// src/main/java/com/afci/training/planning/dto/CandidatureSubmitDTO.java
package com.afci.training.planning.dto;

public class CandidatureSubmitDTO {

    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;
    private String nom;
    private String prenom;

    public CandidatureSubmitDTO() {}

    public CandidatureSubmitDTO(String adresse, String codePostal, String ville,
                                String telephone, String nom, String prenom) {
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.telephone = telephone;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
}
