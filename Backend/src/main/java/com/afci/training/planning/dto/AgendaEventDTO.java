package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class AgendaEventDTO {

    private String id;              // ex: "CONGE-12", "DISPO-7#2025-11-11", "SESS-3"
    private String type;            // SESSION | CONGE | INDISPO | FREE
    private String title;           // Libellé affichable
    private LocalDateTime start;
    private LocalDateTime end;
    private String color;           // Hex (ex: "#ef4444")
    private String recurrence;      // AUCUNE | HEBDO | MENSUELLE | null si non applicable
    private Integer relatedId;      // id_conge / id_disponibilite / id_session

    public AgendaEventDTO() { }

    public AgendaEventDTO(String id, String type, String title,
                          LocalDateTime start, LocalDateTime end,
                          String color, String recurrence, Integer relatedId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.start = start;
        this.end = end;
        this.color = color;
        this.recurrence = recurrence;
        this.relatedId = relatedId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public Integer getRelatedId() { return relatedId; }
    public void setRelatedId(Integer relatedId) { this.relatedId = relatedId; }

    @Override
    public String toString() {
        return "AgendaEventDTO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", color='" + color + '\'' +
                ", recurrence='" + recurrence + '\'' +
                ", relatedId=" + relatedId +
                '}';
    }
}
