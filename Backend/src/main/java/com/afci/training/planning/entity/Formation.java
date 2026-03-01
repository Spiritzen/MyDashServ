package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "formation")
public class Formation {

    public enum Format { PRESENTIEL, DISTANCIEL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formation")
    private Integer idFormation;

    @NotBlank @Size(max = 150)
    @Column(name = "intitule", length = 150, nullable = false)
    private String intitule;

    @Lob
    @Column(name = "objectifs")
    private String objectifs;

    @NotNull
    @Column(name = "duree_heures", nullable = false)
    private Integer dureeHeures;

    @Column(name = "nb_participants_max")
    private Integer nbParticipantsMax;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "format", length = 20, nullable = false)
    private Format format = Format.PRESENTIEL;

    @Size(max = 150)
    @Column(name = "lieu", length = 150)
    private String lieu;

    @Size(max = 120)
    @Column(name = "theme", length = 120)
    private String theme;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif = Boolean.TRUE;

    public Formation() {}

    public Formation(String intitule, String objectifs, Integer dureeHeures, Integer nbParticipantsMax,
                     Format format, String lieu, String theme, Boolean actif) {
        this.intitule = intitule;
        this.objectifs = objectifs;
        this.dureeHeures = dureeHeures;
        this.nbParticipantsMax = nbParticipantsMax;
        this.format = format;
        this.lieu = lieu;
        this.theme = theme;
        this.actif = (actif != null ? actif : Boolean.TRUE);
    }

    public Formation(Integer idFormation, String intitule, String objectifs, Integer dureeHeures, Integer nbParticipantsMax,
                     Format format, String lieu, String theme, Boolean actif) {
        this(intitule, objectifs, dureeHeures, nbParticipantsMax, format, lieu, theme, actif);
        this.idFormation = idFormation;
    }

    public Integer getIdFormation() { return idFormation; }
    public void setIdFormation(Integer idFormation) { this.idFormation = idFormation; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }
    public Integer getDureeHeures() { return dureeHeures; }
    public void setDureeHeures(Integer dureeHeures) { this.dureeHeures = dureeHeures; }
    public Integer getNbParticipantsMax() { return nbParticipantsMax; }
    public void setNbParticipantsMax(Integer nbParticipantsMax) { this.nbParticipantsMax = nbParticipantsMax; }
    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    @Override public String toString() {
        return "Formation{id=" + idFormation + ", intitule='" + intitule + "', dureeHeures=" + dureeHeures +
               ", format=" + format + ", lieu='" + lieu + "', theme='" + theme + "', actif=" + actif + "}";
    }
}
