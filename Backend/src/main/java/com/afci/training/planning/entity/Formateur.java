package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "formateur")
public class Formateur {

    public enum MoyenDeDeplacement { A_PIED, VELO, TRANSPORTS, VOITURE }
    public enum StatutJuridique { AUCUN, AUTOENTREPRENEUR, EURL, SASU, AUTRE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formateur")
    private Integer idFormateur;

    @NotNull
    @Column(name = "heures_max_mensuelles", nullable = false)
    private Integer heuresMaxMensuelles = 0;                 // défaut raisonnable

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif = Boolean.FALSE;                   // inaffectable tant que false

    // ---- Profil métier / exploitation
    @Digits(integer = 8, fraction = 2)
    @Column(name = "taux_horaire", precision = 10, scale = 2)
    private BigDecimal tauxHoraire;

    @Size(max = 255)
    @Column(name = "zone_intervention", length = 255)
    private String zoneIntervention;

    @Min(0)
    @Column(name = "distance_max_km")
    private Integer distanceMaxKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_de_deplacement", length = 20)
    private MoyenDeDeplacement moyenDeDeplacement;

    @Min(0)
    @Column(name = "annees_experience")
    private Integer anneesExperience;

    @Size(max = 500)
    @Column(name = "bio_courte", length = 500)
    private String bioCourte;

    @Size(max = 255)
    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Size(max = 255)
    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_juridique", length = 20)
    private StatutJuridique statutJuridique;

    @Size(max = 32)
    @Column(name = "siret", length = 32)
    private String siret;

    @NotNull
    @Column(name = "rib_fournit", nullable = false)
    private Boolean ribFournit = Boolean.FALSE;

    // ---- Workflow candidature
    @NotNull
    @Column(name = "profil_complet", nullable = false)
    private Boolean profilComplet = Boolean.FALSE;

    @Column(name = "profil_soumis_le")
    private Instant profilSoumisLe;

    // ---- Audit
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ---- Constructeurs
    public Formateur() { }

    public Formateur(Integer heuresMaxMensuelles, Boolean actif) {
        this.heuresMaxMensuelles = (heuresMaxMensuelles != null ? heuresMaxMensuelles : 0);
        this.actif = (actif != null ? actif : Boolean.FALSE);
    }

    public Formateur(Integer idFormateur, Integer heuresMaxMensuelles, Boolean actif) {
        this(heuresMaxMensuelles, actif);
        this.idFormateur = idFormateur;
    }

    // Constructeur complet (utile pour tests/seed)
    public Formateur(Integer idFormateur,
                     Integer heuresMaxMensuelles,
                     Boolean actif,
                     BigDecimal tauxHoraire,
                     String zoneIntervention,
                     Integer distanceMaxKm,
                     MoyenDeDeplacement moyenDeDeplacement,
                     Integer anneesExperience,
                     String bioCourte,
                     String linkedinUrl,
                     String portfolioUrl,
                     StatutJuridique statutJuridique,
                     String siret,
                     Boolean ribFournit,
                     Boolean profilComplet,
                     Instant profilSoumisLe) {
        this.idFormateur = idFormateur;
        this.heuresMaxMensuelles = (heuresMaxMensuelles != null ? heuresMaxMensuelles : 0);
        this.actif = (actif != null ? actif : Boolean.FALSE);
        this.tauxHoraire = tauxHoraire;
        this.zoneIntervention = zoneIntervention;
        this.distanceMaxKm = distanceMaxKm;
        this.moyenDeDeplacement = moyenDeDeplacement;
        this.anneesExperience = anneesExperience;
        this.bioCourte = bioCourte;
        this.linkedinUrl = linkedinUrl;
        this.portfolioUrl = portfolioUrl;
        this.statutJuridique = statutJuridique;
        this.siret = siret;
        this.ribFournit = (ribFournit != null ? ribFournit : Boolean.FALSE);
        this.profilComplet = (profilComplet != null ? profilComplet : Boolean.FALSE);
        this.profilSoumisLe = profilSoumisLe;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        if (this.heuresMaxMensuelles == null) this.heuresMaxMensuelles = 0;
        if (this.actif == null) this.actif = Boolean.FALSE;
        if (this.ribFournit == null) this.ribFournit = Boolean.FALSE;
        if (this.profilComplet == null) this.profilComplet = Boolean.FALSE;
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = Instant.now(); }

    // ---- Getters / Setters
    public Integer getIdFormateur() { return idFormateur; }
    public void setIdFormateur(Integer idFormateur) { this.idFormateur = idFormateur; }
    public Integer getHeuresMaxMensuelles() { return heuresMaxMensuelles; }
    public void setHeuresMaxMensuelles(Integer heuresMaxMensuelles) { this.heuresMaxMensuelles = heuresMaxMensuelles; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal tauxHoraire) { this.tauxHoraire = tauxHoraire; }
    public String getZoneIntervention() { return zoneIntervention; }
    public void setZoneIntervention(String zoneIntervention) { this.zoneIntervention = zoneIntervention; }
    public Integer getDistanceMaxKm() { return distanceMaxKm; }
    public void setDistanceMaxKm(Integer distanceMaxKm) { this.distanceMaxKm = distanceMaxKm; }
    public MoyenDeDeplacement getMoyenDeDeplacement() { return moyenDeDeplacement; }
    public void setMoyenDeDeplacement(MoyenDeDeplacement moyenDeDeplacement) { this.moyenDeDeplacement = moyenDeDeplacement; }
    public Integer getAnneesExperience() { return anneesExperience; }
    public void setAnneesExperience(Integer anneesExperience) { this.anneesExperience = anneesExperience; }
    public String getBioCourte() { return bioCourte; }
    public void setBioCourte(String bioCourte) { this.bioCourte = bioCourte; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public StatutJuridique getStatutJuridique() { return statutJuridique; }
    public void setStatutJuridique(StatutJuridique statutJuridique) { this.statutJuridique = statutJuridique; }
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    public Boolean getRibFournit() { return ribFournit; }
    public void setRibFournit(Boolean ribFournit) { this.ribFournit = ribFournit; }
    public Boolean getProfilComplet() { return profilComplet; }
    public void setProfilComplet(Boolean profilComplet) { this.profilComplet = profilComplet; }
    public Instant getProfilSoumisLe() { return profilSoumisLe; }
    public void setProfilSoumisLe(Instant profilSoumisLe) { this.profilSoumisLe = profilSoumisLe; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Formateur{id=" + idFormateur +
                ", heuresMaxMensuelles=" + heuresMaxMensuelles +
                ", actif=" + actif + "}";
    }
}
