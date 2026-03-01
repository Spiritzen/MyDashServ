// src/main/java/com/afci/training/planning/repository/EvidenceDocRepository.java
package com.afci.training.planning.repository;

import com.afci.training.planning.entity.EvidenceDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenceDocRepository extends JpaRepository<EvidenceDoc, Integer> {

    // Liste par (formateur, compétence)
    List<EvidenceDoc> findByFormateurComp_Formateur_IdFormateurAndFormateurComp_Competence_IdCompetence(
            Integer formateurId, Integer competenceId);

    // Doc par idDoc + contrôle d’appartenance au formateur
    Optional<EvidenceDoc> findByIdDocAndFormateurComp_Formateur_IdFormateur(Integer idDoc, Integer formateurId);
}
