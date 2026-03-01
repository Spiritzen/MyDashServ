package com.afci.training.planning.service;

import com.afci.training.planning.repository.projection.FormateurAffectationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormateurAffectationService {
    Page<FormateurAffectationProjection> listForCurrentFormateur(String email, String statut, Pageable pageable);
    void accept(String email, Integer affectationId);
    void refuse(String email, Integer affectationId);
}
