// src/main/java/com/afci/training/planning/service/AffectationService.java
package com.afci.training.planning.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.AffectationAssignRequest;
import com.afci.training.planning.dto.AffectationCandidateDTO;

/**
 * Contrat du service d’affectation.
 * Implémentation dans com.afci.training.planning.service.impl.AffectationServiceImpl
 */
public interface AffectationService {

    /**
     * Retourne une liste de candidats classés pour la session.
     */
    List<AffectationCandidateDTO> findCandidates(Integer sessionId);

    /**
     * Affecte (directement ou via logique métier interne) un formateur à une session.
     * Le contenu de req doit fournir sessionId et formateurId (via getters).
     */
    @Transactional
    void assign(AffectationAssignRequest req);

    /**
     * Crée une proposition d’affectation (statut PROPOSEE).
     * Idempotent si la proposition existe déjà.
     */
    @Transactional
    void proposer(Integer sessionId, Integer formateurId, String commentaire);

    /**
     * Répond à une proposition (acceptation → CONFIRMEE, refus → ANNULEE).
     * Doit vérifier l’absence de chevauchements avant confirmation.
     */
    @Transactional
    void repondre(Integer affectationId, boolean accept);

    /**
     * Affectation directe (sans proposition) par un admin/gestionnaire.
     * Doit refuser en cas de conflit.
     */
    @Transactional
    void assignerDirect(Integer sessionId, Integer formateurId);
}
