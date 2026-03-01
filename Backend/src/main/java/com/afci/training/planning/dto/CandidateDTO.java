package com.afci.training.planning.dto;

import java.util.List;

public class CandidateDTO {
    private Integer formateurId;
    private String nom;
    private String prenom;
    private String email;

    private List<String> skillsOK;
    private List<String> skillsKO;

    private boolean dispoOK;
    private boolean enConge;
    private int nbConflits;

    private int score;
    private String commentaires;

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
}
