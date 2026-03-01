package com.afci.training.planning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ThemeRequest {
    @NotBlank @Size(max = 120)
    private String name;
    private Boolean active; // optionnel

    public ThemeRequest() {}
    public ThemeRequest(String name, Boolean active) {
        this.name = name; this.active = active;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
