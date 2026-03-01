package com.afci.training.planning.dto;

public class RespondRequest {

    private boolean accept;

    public RespondRequest() {}

    public RespondRequest(boolean accept) {
        this.accept = accept;
    }

    public boolean isAccept() { return accept; }
    public void setAccept(boolean accept) { this.accept = accept; }
}
