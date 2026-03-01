// com.afci.training.planning.repository.DisponibiliteRepository
package com.afci.training.planning.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afci.training.planning.entity.Disponibilite;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Integer> {

    // Liste des indispos qui chevauchent un intervalle (inclusif)
    @Query("""
           select d
           from Disponibilite d
           where d.formateur.idFormateur = :formateurId
             and d.startAt <= :to
             and d.endAt   >= :from
           """)
    List<Disponibilite> findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
        @Param("formateurId") Integer formateurId,
        @Param("to") LocalDateTime to,
        @Param("from") LocalDateTime from
    );

    // Pour l’agenda : celles qui démarrent dans l’intervalle
    @Query("""
           select d
           from Disponibilite d
           where d.formateur.idFormateur = :formateurId
             and d.startAt between :from and :to
           """)
    List<Disponibilite> findByFormateurIdAndStartAtBetween(
        @Param("formateurId") Integer formateurId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    // Test d’overlap rapide (inclusif)
    @Query("""
      select (count(d) > 0)
      from Disponibilite d
      where d.formateur.idFormateur = :fid
        and d.startAt <= :end
        and d.endAt   >= :start
    """)
    boolean existsOverlap(
        @Param("fid") Integer formateurId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    // Variante pour update : exclure l’indispo courante
    @Query("""
      select (count(d) > 0)
      from Disponibilite d
      where d.formateur.idFormateur = :fid
        and d.idDisponibilite <> :currentId
        and d.startAt <= :end
        and d.endAt   >= :start
    """)
    boolean existsOverlapExcluding(
        @Param("fid") Integer formateurId,
        @Param("currentId") Integer currentId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    // Ici on considère tes "disponibilités" comme des périodes d'occupation (missions externes)
    @Query("""
            select d
            from Disponibilite d
            where d.formateur.idFormateur = :fid
              and d.startAt < :end
              and d.endAt   > :start
            """)
     List<Disponibilite> findBusyOverlaps(@Param("fid") Integer formateurId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}
