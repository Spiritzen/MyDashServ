// com.afci.training.planning.repository.CongeRepository
package com.afci.training.planning.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afci.training.planning.entity.Conge;

@Repository
public interface CongeRepository extends JpaRepository<Conge, Integer> {

    // Liste des congés qui chevauchent un intervalle (inclusif)
    @Query("""
           select c
           from Conge c
           where c.formateur.idFormateur = :formateurId
             and c.startAt <= :to
             and c.endAt   >= :from
           """)
    List<Conge> findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
        @Param("formateurId") Integer formateurId,
        @Param("to") LocalDateTime to,
        @Param("from") LocalDateTime from
    );

    // Liste des congés dont le startAt est entre deux bornes (utile pour l’agenda)
    @Query("""
           select c
           from Conge c
           where c.formateur.idFormateur = :formateurId
             and c.startAt between :from and :to
           """)
    List<Conge> findByFormateurIdAndStartAtBetween(
        @Param("formateurId") Integer formateurId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    // Test d’overlap rapide (inclusif) — à utiliser pour les contrôles de conflit
    @Query("""
      select (count(c) > 0)
      from Conge c
      where c.formateur.idFormateur = :fid
        and c.startAt <= :end
        and c.endAt   >= :start
    """)
    boolean existsOverlap(
        @Param("fid") Integer formateurId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    

    @Query("""
            select c
            from Conge c
            where c.formateur.idFormateur = :fid
              and c.startAt < :end
              and c.endAt   > :start
            """)
     List<Conge> findOverlaps(@Param("fid") Integer formateurId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);
}
