package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class UpdateDispoDTO {

    private String commentaire;
    private String recurrence;       // AUCUNE | HEBDO | MENSUELLE
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public UpdateDispoDTO() { }

    public UpdateDispoDTO(String commentaire, String recurrence,
                          LocalDateTime startAt, LocalDateTime endAt) {
        this.commentaire = commentaire;
        this.recurrence = recurrence;
        this.startAt = startAt;
        this.endAt = endAt;
    }

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
        return "UpdateDispoDTO{" +
                "commentaire='" + commentaire + '\'' +
                ", recurrence='" + recurrence + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
