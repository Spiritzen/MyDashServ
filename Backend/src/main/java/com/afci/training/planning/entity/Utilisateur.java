package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "utilisateur",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "formateur_id")
       })
public class Utilisateur {

    public enum Role { ADMIN, GESTIONNAIRE, FORMATEUR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Email
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Size(min = 6, max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.FORMATEUR;          // défaut côté Java

    // --- Workflow / sécurité compte ---
    @Column(name = "compte_valide", nullable = false)
    private boolean compteValide = false;        // candidature non validée

    @Column(name = "email_verifie", nullable = false)
    private boolean emailVerifie = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Utilisateur validatedBy;

    @Column(name = "rgpd_consent_at")
    private Instant rgpdConsentAt;

    // --- Infos identité / contact (déjà exploitées par tes DTO) ---
    @Size(max = 100)
    @Column(length = 100)
    private String nom;

    @Size(max = 100)
    @Column(length = 100)
    private String prenom;

    @Size(max = 255)
    @Column(length = 255)
    private String adresse;

    @Size(max = 120)
    @Column(length = 120)
    private String ville;

    @Size(max = 15)
    @Column(name = "code_postal", length = 15)
    private String codePostal;

    @Size(max = 30)
    @Column(length = 30)
    private String telephone;

    @Size(max = 255)
    @Column(name = "photo_path", length = 255)
    private String photoPath;

    // 0,1 <-> 0,1 lié au profil Formateur (activable pour affectations)
    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "formateur_id", unique = true)
    private Formateur formateur;

    // --- Constructeurs ---
    public Utilisateur() {}

    public Utilisateur(String email, String passwordHash, Role role,
                       String nom, String prenom,
                       String adresse, String ville, String codePostal,
                       String telephone, String photoPath) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = (role != null ? role : Role.FORMATEUR);
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.photoPath = photoPath;
    }

    // --- Hooks ---
    @PrePersist
    protected void onCreate() {
        if (this.role == null) this.role = Role.FORMATEUR;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        if (!this.emailVerifie) this.emailVerifie = false;
        if (!this.compteValide) this.compteValide = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // --- Getters / Setters (pas de variable libre) ---
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isCompteValide() { return compteValide; }
    public void setCompteValide(boolean compteValide) { this.compteValide = compteValide; }

    public boolean isEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Instant getValidatedAt() { return validatedAt; }
    public void setValidatedAt(Instant validatedAt) { this.validatedAt = validatedAt; }

    public Utilisateur getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Utilisateur validatedBy) { this.validatedBy = validatedBy; }

    public Instant getRgpdConsentAt() { return rgpdConsentAt; }
    public void setRgpdConsentAt(Instant rgpdConsentAt) { this.rgpdConsentAt = rgpdConsentAt; }

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

    public Formateur getFormateur() { return formateur; }
    public void setFormateur(Formateur formateur) { this.formateur = formateur; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + idUtilisateur +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", compteValide=" + compteValide +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ville='" + ville + '\'' +
                ", codePostal='" + codePostal + '\'' +
                '}';
    }
}
