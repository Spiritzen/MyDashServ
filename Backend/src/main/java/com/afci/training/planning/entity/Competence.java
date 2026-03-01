package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "competence", uniqueConstraints = @UniqueConstraint(columnNames = "label"))
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competence")
    private Integer idCompetence;

    @NotBlank
    @Size(max = 120)
    @Column(name = "label", length = 120, nullable = false, unique = true)
    private String label;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    // ✅ Nouveau : relation vers Theme (nullable le temps de la migration)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")  // nullable pour l’instant
    private Theme theme;

    // --- Constructeurs ---
    public Competence() {}

    public Competence(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public Competence(Integer idCompetence, String label, String description) {
        this(label, description);
        this.idCompetence = idCompetence;
    }

    // Nouveau constructeur pratique si tu connais déjà le thème
    public Competence(String label, String description, Theme theme) {
        this.label = label;
        this.description = description;
        this.theme = theme;
    }

    // --- Getters / Setters ---
    public Integer getIdCompetence() { return idCompetence; }
    public void setIdCompetence(Integer idCompetence) { this.idCompetence = idCompetence; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }

    @Override
    public String toString() {
        return "Competence{id=" + idCompetence +
               ", label='" + label + '\'' +
               ", themeId=" + (theme != null ? theme.getIdTheme() : null) +
               '}';
    }
}
