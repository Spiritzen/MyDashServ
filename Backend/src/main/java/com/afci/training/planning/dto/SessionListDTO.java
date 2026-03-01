// src/main/java/com/afci/training/planning/dto/SessionListDTO.java
package com.afci.training.planning.dto;

import com.afci.training.planning.entity.Session.Mode;
import com.afci.training.planning.entity.Session.Statut;
import java.time.LocalDateTime;

public class SessionListDTO {
    private Integer id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String formationLabel;  // ex: f.getIntitule()
    private String formateurLabel;  // ex: "Dupont Alice" si affecté
    private Mode mode;
    private Statut statut;
    private String ville;
    private String salle;

    public SessionListDTO() {}

    public SessionListDTO(
            Integer id,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            String formationLabel,
            String formateurLabel,
            Mode mode,
            Statut statut,
            String ville,
            String salle
    ) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.formationLabel = formationLabel;
        this.formateurLabel = formateurLabel;
        this.mode = mode;
        this.statut = statut;
        this.ville = ville;
        this.salle = salle;
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getFormationLabel() { return formationLabel; }
    public void setFormationLabel(String formationLabel) { this.formationLabel = formationLabel; }

    public String getFormateurLabel() { return formateurLabel; }
    public void setFormateurLabel(String formateurLabel) { this.formateurLabel = formateurLabel; }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
}
