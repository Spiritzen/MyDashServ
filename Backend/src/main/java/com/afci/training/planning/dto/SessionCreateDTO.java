// src/main/java/com/afci/training/planning/dto/SessionCreateDTO.java
package com.afci.training.planning.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload de création d’une session.
 * Correspond aux champs envoyés par la modale "Nouvelle session".
 *
 * Règle : si ville/salle/mode sont null côté payload,
 *         le contrôleur peut préremplir avec les valeurs par défaut de la Formation.
 */
public class SessionCreateDTO {

    @NotNull(message = "formationId est obligatoire")
    private Integer formationId;

    @NotNull(message = "dateDebut est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "dateFin est obligatoire")
    private LocalDateTime dateFin;

    // "PRESENTIEL" | "DISTANCIEL" | "HYBRIDE"
    // Laisse null pour utiliser le mode par défaut de la formation
    @Size(max = 30)
    private String mode;

    // Laisse null pour utiliser la ville par défaut de la formation
    @Size(max = 150)
    private String ville;

    // Laisse null pour utiliser la salle par défaut de la formation
    @Size(max = 150)
    private String salle;

    // ----- ctors -----
    public SessionCreateDTO() {}

    public SessionCreateDTO(
            Integer formationId,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            String mode,
            String ville,
            String salle
    ) {
        this.formationId = formationId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.mode = mode;
        this.ville = ville;
        this.salle = salle;
    }

    // ----- getters/setters -----
    public Integer getFormationId() { return formationId; }
    public void setFormationId(Integer formationId) { this.formationId = formationId; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
}
