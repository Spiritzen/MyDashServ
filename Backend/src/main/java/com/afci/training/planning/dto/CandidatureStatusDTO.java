// src/main/java/com/afci/training/planning/dto/CandidatureStatusDTO.java
package com.afci.training.planning.dto;

import com.afci.training.planning.enums.StatutCandidature;

public class CandidatureStatusDTO {

    private StatutCandidature statut;
    private boolean profilComplet;
    private boolean emailVerifie;
    private boolean compteValide;
    private int nbCompetences;
    private String message;

    public CandidatureStatusDTO() {}

    public CandidatureStatusDTO(StatutCandidature statut, boolean profilComplet,
                                boolean emailVerifie, boolean compteValide,
                                int nbCompetences, String message) {
        this.statut = statut;
        this.profilComplet = profilComplet;
        this.emailVerifie = emailVerifie;
        this.compteValide = compteValide;
        this.nbCompetences = nbCompetences;
        this.message = message;
    }

    public StatutCandidature getStatut() { return statut; }
    public void setStatut(StatutCandidature statut) { this.statut = statut; }

    public boolean isProfilComplet() { return profilComplet; }
    public void setProfilComplet(boolean profilComplet) { this.profilComplet = profilComplet; }

    public boolean isEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public boolean isCompteValide() { return compteValide; }
    public void setCompteValide(boolean compteValide) { this.compteValide = compteValide; }

    public int getNbCompetences() { return nbCompetences; }
    public void setNbCompetences(int nbCompetences) { this.nbCompetences = nbCompetences; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
