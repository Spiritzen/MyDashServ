package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "affectation",
    uniqueConstraints = @UniqueConstraint(columnNames = {"session_id","formateur_id"})
)
public class Affectation {

    public enum Statut { PROPOSEE, CONFIRMEE, ANNULEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_affectation")
    private Integer idAffectation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    private Formateur formateur;

    @NotNull
    @Column(name = "heures_prevues", nullable = false)
    private Integer heuresPrevues;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private Statut statut = Statut.CONFIRMEE;

    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Affectation() {}

    public Affectation(Session session, Formateur formateur, Integer heuresPrevues, Statut statut, String commentaire) {
        this.session = session; this.formateur = formateur; this.heuresPrevues = heuresPrevues;
        this.statut = (statut != null ? statut : Statut.CONFIRMEE);
        this.commentaire = commentaire;
    }

    public Affectation(Integer id, Session s, Formateur f, Integer h, Statut st, String c, LocalDateTime createdAt) {
        this(s, f, h, st, c); this.idAffectation = id;
        if (createdAt != null) this.createdAt = createdAt;
    }

    public Integer getIdAffectation() { return idAffectation; }
    public void setIdAffectation(Integer idAffectation) { this.idAffectation = idAffectation; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public Formateur getFormateur() { return formateur; }
    public void setFormateur(Formateur formateur) { this.formateur = formateur; }
    public Integer getHeuresPrevues() { return heuresPrevues; }
    public void setHeuresPrevues(Integer heuresPrevues) { this.heuresPrevues = heuresPrevues; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override public String toString() {
        return "Affectation{id=" + idAffectation + ", sessionId=" +
               (session!=null?session.getIdSession():null) +
               ", formateurId=" + (formateur!=null?formateur.getIdFormateur():null) +
               ", heuresPrevues=" + heuresPrevues + ", statut=" + statut + "}";
    }
}
