package com.afci.training.planning.entity;

import java.io.Serializable;
import java.util.Objects;

public class FormateurCompId implements Serializable {
    private Integer formateur;
    private Integer competence;

    public FormateurCompId() {}
    public FormateurCompId(Integer formateur, Integer competence) {
        this.formateur = formateur; this.competence = competence;
    }
    public Integer getFormateur() { return formateur; }
    public void setFormateur(Integer formateur) { this.formateur = formateur; }
    public Integer getCompetence() { return competence; }
    public void setCompetence(Integer competence) { this.competence = competence; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormateurCompId that)) return false;
        return Objects.equals(formateur, that.formateur) && Objects.equals(competence, that.competence);
    }
    @Override public int hashCode() { return Objects.hash(formateur, competence); }
}
