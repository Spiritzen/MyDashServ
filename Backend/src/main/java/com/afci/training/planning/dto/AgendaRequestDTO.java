package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class AgendaRequestDTO {

    private Integer formateurId;
    private LocalDateTime from;
    private LocalDateTime to;
    private Boolean withFree;

    public AgendaRequestDTO() { }

    public AgendaRequestDTO(Integer formateurId, LocalDateTime from,
                            LocalDateTime to, Boolean withFree) {
        this.formateurId = formateurId;
        this.from = from;
        this.to = to;
        this.withFree = withFree;
    }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public LocalDateTime getFrom() { return from; }
    public void setFrom(LocalDateTime from) { this.from = from; }

    public LocalDateTime getTo() { return to; }
    public void setTo(LocalDateTime to) { this.to = to; }

    public Boolean getWithFree() { return withFree; }
    public void setWithFree(Boolean withFree) { this.withFree = withFree; }

    @Override
    public String toString() {
        return "AgendaRequestDTO{" +
                "formateurId=" + formateurId +
                ", from=" + from +
                ", to=" + to +
                ", withFree=" + withFree +
                '}';
    }
}
