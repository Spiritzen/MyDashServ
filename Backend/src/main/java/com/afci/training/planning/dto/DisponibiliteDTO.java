package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class DisponibiliteDTO {

    private Integer idDisponibilite;
    private Integer formateurId;
    private String commentaire;
    private String recurrence;       // AUCUNE | HEBDO | MENSUELLE
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public DisponibiliteDTO() { }

    public DisponibiliteDTO(Integer idDisponibilite, Integer formateurId,
                            String commentaire, String recurrence,
                            LocalDateTime startAt, LocalDateTime endAt) {
        this.idDisponibilite = idDisponibilite;
        this.formateurId = formateurId;
        this.commentaire = commentaire;
        this.recurrence = recurrence;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Integer getIdDisponibilite() { return idDisponibilite; }
    public void setIdDisponibilite(Integer idDisponibilite) { this.idDisponibilite = idDisponibilite; }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    @Override
    public String toString() {
        return "DisponibiliteDTO{" +
                "idDisponibilite=" + idDisponibilite +
                ", formateurId=" + formateurId +
                ", commentaire='" + commentaire + '\'' +
                ", recurrence='" + recurrence + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
