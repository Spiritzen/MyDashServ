package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class UpdateCongeDTO {

    private String motif;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public UpdateCongeDTO() { }

    public UpdateCongeDTO(String motif, LocalDateTime startAt, LocalDateTime endAt) {
        this.motif = motif;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    @Override
    public String toString() {
        return "UpdateCongeDTO{" +
                "motif='" + motif + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
