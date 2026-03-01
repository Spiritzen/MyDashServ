package com.afci.training.planning.dto;

public class UtilisateurDTO {

    private Integer idUtilisateur;
    private String email;
    private String role;          // optionnel : utile pour d’autres écrans

    private String nom;
    private String prenom;
    private String adresse;
    private String ville;
    private String codePostal;
    private String telephone;

    // Le front lit 'photoUrl' (pas 'photoPath')
    private String photoUrl;      // ex: "/files/users/abc.jpg"

    // Le front lit ces flags
    private Boolean compteValide; // null/true/false
    private Boolean emailVerifie; // null/true/false

    // optionnel
    private Integer formateurId;

    public UtilisateurDTO() {}

    public UtilisateurDTO(Integer idUtilisateur, String email, String role,
                          String nom, String prenom,
                          String adresse, String ville, String codePostal,
                          String telephone, String photoUrl,
                          Boolean compteValide, Boolean emailVerifie,
                          Integer formateurId) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.photoUrl = photoUrl;
        this.compteValide = compteValide;
        this.emailVerifie = emailVerifie;
        this.formateurId = formateurId;
    }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

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

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Boolean getCompteValide() { return compteValide; }
    public void setCompteValide(Boolean compteValide) { this.compteValide = compteValide; }

    public Boolean getEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(Boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }
}
