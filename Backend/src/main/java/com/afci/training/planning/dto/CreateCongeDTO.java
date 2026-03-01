package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class CreateCongeDTO {

    private Integer formateurId;
    private String motif;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public CreateCongeDTO() { }

    public CreateCongeDTO(Integer formateurId, String motif,
                          LocalDateTime startAt, LocalDateTime endAt) {
        this.formateurId = formateurId;
        this.motif = motif;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    @Override
    public String toString() {
        return "CreateCongeDTO{" +
                "formateurId=" + formateurId +
                ", motif='" + motif + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
