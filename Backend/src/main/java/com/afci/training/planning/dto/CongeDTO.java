package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class CongeDTO {

    private Integer idConge;
    private Integer formateurId;
    private String motif;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public CongeDTO() { }

    public CongeDTO(Integer idConge, Integer formateurId, String motif,
                    LocalDateTime startAt, LocalDateTime endAt) {
        this.idConge = idConge;
        this.formateurId = formateurId;
        this.motif = motif;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Integer getIdConge() { return idConge; }
    public void setIdConge(Integer idConge) { this.idConge = idConge; }

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
        return "CongeDTO{" +
                "idConge=" + idConge +
                ", formateurId=" + formateurId +
                ", motif='" + motif + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
