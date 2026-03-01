// src/main/java/com/afci/training/planning/dto/AffectationAssignRequest.java
package com.afci.training.planning.dto;

import jakarta.validation.constraints.NotNull;

public class AffectationAssignRequest {

    @NotNull
    private Integer sessionId;

    @NotNull
    private Integer formateurId;

    /** Requis par Jackson/Spring. */
    public AffectationAssignRequest() { }

    public AffectationAssignRequest(Integer sessionId, Integer formateurId) {
        this.sessionId = sessionId;
        this.formateurId = formateurId;
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getFormateurId() { return formateurId; }
    public void setFormateurId(Integer formateurId) { this.formateurId = formateurId; }
}
