package com.afci.training.planning.repository;

import com.afci.training.planning.entity.Affectation;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.repository.projection.FormateurAffectationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AffectationRepository extends JpaRepository<Affectation, Integer> {

    // ---- Existence & listing simples
    boolean existsBySession_IdSessionAndFormateur_IdFormateurAndStatut(
            Integer sessionId, Integer formateurId, Affectation.Statut statut);

    List<Affectation> findBySession_IdSession(Integer sessionId);

    // ---- Détection de chevauchement sur affectations CONFIRMEES (booléen)
    @Query("""
           select (count(a) > 0)
           from Affectation a
           where a.formateur.idFormateur = :formateurId
             and a.statut = com.afci.training.planning.entity.Affectation.Statut.CONFIRMEE
             and a.session.dateDebut <= :end
             and a.session.dateFin   >= :start
           """)
    boolean existsOverlap(@Param("formateurId") Integer formateurId,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    // ---- Sessions confirmées d’un formateur dans un intervalle (pour l’agenda)
    @Query("""
           select s
           from Affectation a
             join a.session s
           where a.formateur.idFormateur = :formateurId
             and a.statut = com.afci.training.planning.entity.Affectation.Statut.CONFIRMEE
             and s.dateDebut <= :to
             and s.dateFin   >= :from
           """)
    List<Session> findConfirmedSessionsByFormateurBetween(@Param("formateurId") Integer formateurId,
                                                         @Param("from") LocalDateTime from,
                                                         @Param("to") LocalDateTime to);

    // ---- Compter les conflits (PROPOSEE/CONFIRMEE) sur la fenêtre
    @Query("""
           select count(a)
           from Affectation a
           where a.formateur.idFormateur = :fid
             and a.session.dateDebut < :end
             and a.session.dateFin   > :start
             and a.statut in (
               com.afci.training.planning.entity.Affectation.Statut.PROPOSEE,
               com.afci.training.planning.entity.Affectation.Statut.CONFIRMEE
             )
           """)
    long countConflicts(@Param("fid") Integer formateurId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

    // ---- Existence d’une affectation (peu importe le statut)
    @Query("""
           select (count(a) > 0)
           from Affectation a
           where a.session.idSession = :sid
             and a.formateur.idFormateur = :fid
           """)
    boolean existsBySessionIdAndFormateurId(@Param("sid") Integer sessionId,
                                           @Param("fid") Integer formateurId);

    // =========================================================
    // ✅ LISTE FORMATEUR : filtre sur le STATUT DE SESSION
    //    (EN_COURS => onglet "Proposées", PLANIFIEE => "Acceptées", ANNULEE => "Refusées")
    // =========================================================
    @Query(value = """
        select
          a.id_affectation as affectationId,
          a.statut         as affectationStatut,
          a.commentaire    as commentaire,
          a.created_at     as createdAt,

          s.id_session     as sessionId,
          s.date_debut     as dateDebut,
          s.date_fin       as dateFin,
          s.mode           as mode,
          s.statut         as sessionStatut,
          s.ville          as ville,
          s.salle          as salle,

          f.id_formation   as formationId,
          f.intitule       as formationIntitule
        from affectation a
        join session s on s.id_session = a.session_id
        join formation f on f.id_formation = s.formation_id
        where a.formateur_id = :formateurId
          and (:sessionStatut is null or s.statut = :sessionStatut)
        order by s.date_debut asc
        """,
        countQuery = """
        select count(*)
        from affectation a
        join session s on s.id_session = a.session_id
        where a.formateur_id = :formateurId
          and (:sessionStatut is null or s.statut = :sessionStatut)
        """,
        nativeQuery = true)
    Page<FormateurAffectationProjection> findFormateurAffectations(
            @Param("formateurId") Integer formateurId,
            @Param("sessionStatut") String sessionStatut,
            Pageable pageable
    );
}
