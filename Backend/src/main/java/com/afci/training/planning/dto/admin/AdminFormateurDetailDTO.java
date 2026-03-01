package com.afci.training.planning.dto.admin;

import java.time.Instant;
import java.util.List;

public class AdminFormateurDetailDTO {

    private Integer idUtilisateur;
    private String email;
    private String nom;
    private String prenom;
    private String adresse;
    private String ville;
    private String codePostal;
    private String telephone;

    private boolean emailVerifie;
    private boolean compteValide;
    private String photoUrl;

    private Instant profilSoumisLe;

    // ✅ liste “riche” pour l’admin (avec titre, description, années d’exp., docs…)
    private List<AdminCompetenceItemDTO> competences;

    public AdminFormateurDetailDTO() {}

    public AdminFormateurDetailDTO(Integer idUtilisateur,
                                   String email,
                                   String nom,
                                   String prenom,
                                   String adresse,
                                   String ville,
                                   String codePostal,
                                   String telephone,
                                   boolean emailVerifie,
                                   boolean compteValide,
                                   String photoUrl,
                                   Instant profilSoumisLe,
                                   List<AdminCompetenceItemDTO> competences) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.emailVerifie = emailVerifie;
        this.compteValide = compteValide;
        this.photoUrl = photoUrl;
        this.profilSoumisLe = profilSoumisLe;
        this.competences = competences;
    }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public boolean isEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public boolean isCompteValide() { return compteValide; }
    public void setCompteValide(boolean compteValide) { this.compteValide = compteValide; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Instant getProfilSoumisLe() { return profilSoumisLe; }
    public void setProfilSoumisLe(Instant profilSoumisLe) { this.profilSoumisLe = profilSoumisLe; }

    public List<AdminCompetenceItemDTO> getCompetences() { return competences; }
    public void setCompetences(List<AdminCompetenceItemDTO> competences) { this.competences = competences; }
}
