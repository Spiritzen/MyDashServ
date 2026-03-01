// src/main/java/com/afci/training/planning/repository/FormateurCompRepository.java
package com.afci.training.planning.repository;

import com.afci.training.planning.entity.FormateurComp;
import com.afci.training.planning.entity.FormateurCompId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FormateurCompRepository extends JpaRepository<FormateurComp, FormateurCompId> {

    // --- basiques ---
    List<FormateurComp> findByFormateur_IdFormateur(Integer idFormateur);

    boolean existsByFormateur_IdFormateurAndCompetence_IdCompetence(Integer formateurId, Integer competenceId);

    Optional<FormateurComp> findByFormateur_IdFormateurAndCompetence_IdCompetence(Integer formateurId, Integer competenceId);

    @Query("select count(fc) from FormateurComp fc where fc.formateur.idFormateur = :id")
    int countByFormateurId(@Param("id") Integer formateurId);

    @Query("""
            select fc
            from FormateurComp fc
            where fc.formateur.idFormateur = :fid
            """)
    List<FormateurComp> findAllByFormateurId(@Param("fid") Integer formateurId);

    // --- filtrage matching ---
    // On exclut un statut passé en paramètre (ex.: REJECTED), on accepte PENDING/VALIDATED.
    // On ajoute "or fc.status is null" pour compat avec anciennes données éventuelles.
    @Query("""
            select fc
            from FormateurComp fc
            where fc.formateur.idFormateur = :fid
              and fc.visible = true
              and fc.status <> :excludedStatus
              and lower(fc.competence.label) in :labelsLower
        """)
        List<FormateurComp> findVisibleByLabelsExcludingStatus(
            @Param("fid") Integer formateurId,
            @Param("labelsLower") Set<String> labelsLower,
            @Param("excludedStatus") FormateurComp.CompetenceStatus excludedStatus
        );
    

    // Helper conservant l’ancienne signature utilisée côté service
    default List<FormateurComp> findVisibleNonRejectedByLabels(Integer formateurId, Set<String> labelsLower) {
        return findVisibleByLabelsExcludingStatus(formateurId, labelsLower, FormateurComp.CompetenceStatus.REJECTED);
    }
    
}
