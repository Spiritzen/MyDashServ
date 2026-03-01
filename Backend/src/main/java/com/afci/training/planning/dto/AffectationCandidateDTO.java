// src/main/java/com/afci/training/planning/dto/AffectationCandidateDTO.java
package com.afci.training.planning.dto;

import java.util.List;

public class AffectationCandidateDTO {

    // --- Identité formateur
    private Integer formateurId;
    private String prenom;
    private String nom;
    private String email;

    // --- Matching
    private double score;
    private List<String> skillsOK;
    private List<String> skillsKO;

    // --- Disponibilité / Conflits
    private boolean dispoOK;   // pas de mission externe chevauchante
    private boolean enConge;   // vrai si congé chevauche
    private int nbConflits;    // nb d’affectations (PROPOSEE/CONFIRMEE) chevauchantes

    // --- Divers
    private String commentaires; // texte court optionnel

    /** Constructeur no-args requis par Jackson/Spring. */
    public AffectationCandidateDTO() { }

    /** Constructeur complet pratique pour le mapping en service. */
    public AffectationCandidateDTO(
            Integer formateurId,
            String prenom,
            String nom,
            String email,
            double score,
            List<String> skillsOK,
            List<String> skillsKO,
            boolean dispoOK,
            boolean enConge,
            int nbConflits,
            String commentaires
    ) {
        this.formateurId = formateurId;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.score = score;
        this.skillsOK = skillsOK;
        this.skillsKO = skillsKO;
        this.dispoOK = dispoOK;
        this.enConge = enConge;
        this.nbConflits = nbConflits;
        this.commentaires = commentaires;
    }

    // --- Getters / Setters

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public List<String> getSkillsOK() { return skillsOK; }
    public void setSkillsOK(List<String> skillsOK) { this.skillsOK = skillsOK; }

    public List<String> getSkillsKO() { return skillsKO; }
    public void setSkillsKO(List<String> skillsKO) { this.skillsKO = skillsKO; }

    public boolean isDispoOK() { return dispoOK; }
    public void setDispoOK(boolean dispoOK) { this.dispoOK = dispoOK; }

    public boolean isEnConge() { return enConge; }
    public void setEnConge(boolean enConge) { this.enConge = enConge; }

    public int getNbConflits() { return nbConflits; }
    public void setNbConflits(int nbConflits) { this.nbConflits = nbConflits; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
}
