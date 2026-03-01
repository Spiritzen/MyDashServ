package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {

    public enum Mode { PRESENTIEL, DISTANCIEL, HYBRIDE }
    public enum Statut { PLANIFIEE, AFFECTEE, EN_COURS, TERMINEE, ANNULEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Integer idSession;

    @NotNull
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @NotNull
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 20, nullable = false)
    private Mode mode = Mode.PRESENTIEL;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private Statut statut = Statut.PLANIFIEE;

    @Size(max = 100)
    @Column(name = "ville", length = 100)
    private String ville;

    @Size(max = 100)
    @Column(name = "salle", length = 100)
    private String salle;

    // ---- FK simplifiée (id) : on ne conserve QUE formation_id ici
    @NotNull
    @Column(name = "formation_id", nullable = false)
    private Integer formationId;

    // === Getters / Setters ===
    public Integer getIdSession() { return idSession; }
    public void setIdSession(Integer idSession) { this.idSession = idSession; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }

    public Integer getFormationId() { return formationId; }
    public void setFormationId(Integer formationId) { this.formationId = formationId; }
}
