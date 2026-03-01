package com.afci.training.planning.dto;

public class CompetenceDTO {
    private Integer idCompetence;
    private String label;
    private String description;
    private Integer themeId;
    private String themeName;

    public CompetenceDTO() {}

    public CompetenceDTO(Integer idCompetence, String label, String description, Integer themeId, String themeName) {
        this.idCompetence = idCompetence;
        this.label = label;
        this.description = description;
        this.themeId = themeId;
        this.themeName = themeName;
    }

    public Integer getIdCompetence() { return idCompetence; }
    public void setIdCompetence(Integer idCompetence) { this.idCompetence = idCompetence; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public String getThemeName() { return themeName; }
    public void setThemeName(String themeName) { this.themeName = themeName; }
}
