package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conge")
public class Conge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conge")
    private Integer idConge;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    private Formateur formateur;

    @NotNull
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @NotNull
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "motif", length = 120)
    private String motif;

    public Conge() {}
    public Conge(Formateur formateur, LocalDateTime startAt, LocalDateTime endAt, String motif) {
        this.formateur = formateur; this.startAt = startAt; this.endAt = endAt; this.motif = motif;
    }
    public Conge(Integer idConge, Formateur f, LocalDateTime s, LocalDateTime e, String m) {
        this(f, s, e, m); this.idConge = idConge;
    }

    public Integer getIdConge() { return idConge; }
    public void setIdConge(Integer idConge) { this.idConge = idConge; }
    public Formateur getFormateur() { return formateur; }
    public void setFormateur(Formateur formateur) { this.formateur = formateur; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    @Override public String toString() {
        return "Conge{id=" + idConge + ", formateurId=" +
               (formateur!=null?formateur.getIdFormateur():null) +
               ", startAt=" + startAt + ", endAt=" + endAt + ", motif='" + motif + "'}";
    }
}
