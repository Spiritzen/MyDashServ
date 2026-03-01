package com.afci.training.planning.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.EligibilityResultDTO;
import com.afci.training.planning.entity.Conge;
import com.afci.training.planning.entity.Disponibilite;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.CongeRepository;
import com.afci.training.planning.repository.DisponibiliteRepository;

@Service
@Transactional(readOnly = true)
public class AffectationCheckService {

    private final AffectationRepository affectationRepository;
    private final CongeRepository congeRepository;
    private final DisponibiliteRepository dispoRepository;

    public AffectationCheckService(AffectationRepository affectationRepository,
                                   CongeRepository congeRepository,
                                   DisponibiliteRepository dispoRepository) {
        this.affectationRepository = affectationRepository;
        this.congeRepository = congeRepository;
        this.dispoRepository = dispoRepository;
    }

    public EligibilityResultDTO canAssign(Integer formateurId, LocalDateTime start, LocalDateTime end) {
        // ✅ Sessions affectées (source de vérité : Affectation CONFIRMEE)
        if (affectationRepository.existsOverlap(formateurId, start, end)) {
            return new EligibilityResultDTO(Boolean.FALSE, "Chevauche une session existante.");
        }

        // Congés
        List<Conge> conges = congeRepository
            .findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(formateurId, end, start);
        if (!conges.isEmpty()) {
            return new EligibilityResultDTO(Boolean.FALSE, "Chevauche un congé.");
        }

        // Indispos
        List<Disponibilite> indispos = dispoRepository
            .findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(formateurId, end, start);
        if (!indispos.isEmpty()) {
            return new EligibilityResultDTO(Boolean.FALSE, "Chevauche une indisponibilité.");
        }

        return new EligibilityResultDTO(Boolean.TRUE, "OK");
    }
}
