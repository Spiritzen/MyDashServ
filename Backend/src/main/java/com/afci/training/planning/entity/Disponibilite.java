package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilite")
public class Disponibilite {

    public enum Recurrence { AUCUNE, HEBDO, MENSUELLE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilite")
    private Integer idDisponibilite;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    private Formateur formateur;

    @NotNull
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @NotNull
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence", length = 20, nullable = false)
    private Recurrence recurrence = Recurrence.AUCUNE;

    @Column(name = "commentaire", length = 255)
    private String commentaire;

    public Disponibilite() {}

    public Disponibilite(Formateur formateur, LocalDateTime startAt, LocalDateTime endAt,
                         Recurrence recurrence, String commentaire) {
        this.formateur = formateur;
        this.startAt = startAt;
        this.endAt = endAt;
        this.recurrence = (recurrence != null ? recurrence : Recurrence.AUCUNE);
        this.commentaire = commentaire;
    }

    public Disponibilite(Integer id, Formateur f, LocalDateTime s, LocalDateTime e, Recurrence r, String c) {
        this(f, s, e, r, c); this.idDisponibilite = id;
    }

    public Integer getIdDisponibilite() { return idDisponibilite; }
    public void setIdDisponibilite(Integer idDisponibilite) { this.idDisponibilite = idDisponibilite; }
    public Formateur getFormateur() { return formateur; }
    public void setFormateur(Formateur formateur) { this.formateur = formateur; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public Recurrence getRecurrence() { return recurrence; }
    public void setRecurrence(Recurrence recurrence) { this.recurrence = recurrence; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    @Override public String toString() {
        return "Disponibilite{id=" + idDisponibilite + ", formateurId=" +
               (formateur!=null?formateur.getIdFormateur():null) +
               ", startAt=" + startAt + ", endAt=" + endAt + ", recurrence=" + recurrence + "}";
    }
}
