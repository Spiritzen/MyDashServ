package com.afci.training.planning.dto;

public class PropositionRequest {

    private Integer formateurId;
    private String note;

    public PropositionRequest() {}

    public PropositionRequest(Integer formateurId, String note) {
        this.formateurId = formateurId;
        this.note = note;
    }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
