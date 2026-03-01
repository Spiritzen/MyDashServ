package com.afci.training.planning.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UtilisateurCreateDTO {
    @Email @NotBlank @Size(max=150)
    private String email;

    @NotBlank @Size(min=6, max=255)
    private String password;

 

    // NEW
    @Size(max=80) private String nom;
    @Size(max=80) private String prenom;

    private String adresse;
    private String ville;
    private String codePostal;
    private String telephone;
    private String photoPath;

    public UtilisateurCreateDTO() { }

    public UtilisateurCreateDTO(String email, String password,
                                String nom, String prenom,
                                String adresse, String ville, String codePostal,
                                String telephone, String photoPath) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.photoPath = photoPath;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
}
