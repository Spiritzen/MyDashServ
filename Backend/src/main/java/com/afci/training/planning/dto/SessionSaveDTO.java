// src/main/java/com/afci/training/planning/dto/SessionSaveDTO.java
package com.afci.training.planning.dto;

import com.afci.training.planning.entity.Session.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SessionSaveDTO {

    @NotNull
    private Integer formationId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")                   // <-- important
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")                // <-- pour @RequestBody / validation
    private LocalDateTime dateDebut;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")                   // <-- important
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateFin;

    @NotNull
    private Mode mode; // PRESENTIEL / DISTANCIEL / HYBRIDE

    private String ville;
    private String salle;

    public SessionSaveDTO() {}

    public Integer getFormationId() { return formationId; }
    public void setFormationId(Integer formationId) { this.formationId = formationId; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
}
