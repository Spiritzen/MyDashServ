package com.afci.training.planning.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class FormateurDTO {
    private Integer idFormateur;
    private Integer heuresMaxMensuelles;
    private Boolean actif;

    private BigDecimal tauxHoraire;
    private String zoneIntervention;
    private Integer distanceMaxKm;
    private String moyenDeDeplacement; // enum name
    private Integer anneesExperience;
    private String bioCourte;
    private String linkedinUrl;
    private String portfolioUrl;
    private String statutJuridique;    // enum name
    private String siret;
    private Boolean ribFournit;
    private Boolean profilComplet;
    private Instant profilSoumisLe;

    private Instant createdAt;
    private Instant updatedAt;

    public FormateurDTO() { }

    public FormateurDTO(Integer idFormateur, Integer heuresMaxMensuelles, Boolean actif,
                        BigDecimal tauxHoraire, String zoneIntervention, Integer distanceMaxKm,
                        String moyenDeDeplacement, Integer anneesExperience, String bioCourte,
                        String linkedinUrl, String portfolioUrl, String statutJuridique,
                        String siret, Boolean ribFournit, Boolean profilComplet, Instant profilSoumisLe,
                        Instant createdAt, Instant updatedAt) {
        this.idFormateur = idFormateur;
        this.heuresMaxMensuelles = heuresMaxMensuelles;
        this.actif = actif;
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
        this.ribFournit = ribFournit;
        this.profilComplet = profilComplet;
        this.profilSoumisLe = profilSoumisLe;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
    public String getMoyenDeDeplacement() { return moyenDeDeplacement; }
    public void setMoyenDeDeplacement(String moyenDeDeplacement) { this.moyenDeDeplacement = moyenDeDeplacement; }
    public Integer getAnneesExperience() { return anneesExperience; }
    public void setAnneesExperience(Integer anneesExperience) { this.anneesExperience = anneesExperience; }
    public String getBioCourte() { return bioCourte; }
    public void setBioCourte(String bioCourte) { this.bioCourte = bioCourte; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getStatutJuridique() { return statutJuridique; }
    public void setStatutJuridique(String statutJuridique) { this.statutJuridique = statutJuridique; }
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
}
