package com.afci.training.planning.dto;

public class UtilisateurUpdateDTO {

    private String nom;
    private String prenom;
    private String adresse;
    private String ville;
    private String codePostal;
    private String telephone;
    private String photoPath;

    // Champs admin optionnels
    private String role;           // ADMIN | GESTIONNAIRE | FORMATEUR
    private Boolean compteValide;
    private Boolean emailVerifie;

    public UtilisateurUpdateDTO() { }

    public UtilisateurUpdateDTO(String nom, String prenom,
                                String adresse, String ville, String codePostal,
                                String telephone, String photoPath,
                                String role, Boolean compteValide, Boolean emailVerifie) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.photoPath = photoPath;
        this.role = role;
        this.compteValide = compteValide;
        this.emailVerifie = emailVerifie;
    }

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
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getCompteValide() { return compteValide; }
    public void setCompteValide(Boolean compteValide) { this.compteValide = compteValide; }
    public Boolean getEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(Boolean emailVerifie) { this.emailVerifie = emailVerifie; }
}
