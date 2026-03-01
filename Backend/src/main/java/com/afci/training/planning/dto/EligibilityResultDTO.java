package com.afci.training.planning.dto;

public class EligibilityResultDTO {

    private Boolean canAssign;
    private String reason;

    public EligibilityResultDTO() { }

    public EligibilityResultDTO(Boolean canAssign, String reason) {
        this.canAssign = canAssign;
        this.reason = reason;
    }

    public Boolean getCanAssign() { return canAssign; }
    public void setCanAssign(Boolean canAssign) { this.canAssign = canAssign; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "EligibilityResultDTO{" +
                "canAssign=" + canAssign +
                ", reason='" + reason + '\'' +
                '}';
    }
}
