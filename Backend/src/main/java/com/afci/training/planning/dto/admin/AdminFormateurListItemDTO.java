package com.afci.training.planning.dto.admin;

import java.time.Instant;

public class AdminFormateurListItemDTO {
    private Integer idUtilisateur;
    private String email;
    private String nom;
    private String prenom;
    private boolean emailVerifie;
    private boolean compteValide;
    private String photoUrl;
    private Instant profilSoumisLe; // même type que sur l'entité Formateur
    private int nbCompetences;

    public AdminFormateurListItemDTO() {}

    public AdminFormateurListItemDTO(Integer idUtilisateur, String email, String nom, String prenom,
                                     boolean emailVerifie, boolean compteValide, String photoUrl,
                                     Instant profilSoumisLe, int nbCompetences) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.emailVerifie = emailVerifie;
        this.compteValide = compteValide;
        this.photoUrl = photoUrl;
        this.profilSoumisLe = profilSoumisLe;
        this.nbCompetences = nbCompetences;
    }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public boolean isEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public boolean isCompteValide() { return compteValide; }
    public void setCompteValide(boolean compteValide) { this.compteValide = compteValide; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Instant getProfilSoumisLe() { return profilSoumisLe; }
    public void setProfilSoumisLe(Instant profilSoumisLe) { this.profilSoumisLe = profilSoumisLe; }

    public int getNbCompetences() { return nbCompetences; }
    public void setNbCompetences(int nbCompetences) { this.nbCompetences = nbCompetences; }
}
