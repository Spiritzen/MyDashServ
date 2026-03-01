package com.afci.training.planning.controller.formateur;

import com.afci.training.planning.entity.Session;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.UtilisateurRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs/me")
@PreAuthorize("hasAnyRole('FORMATEUR','ADMIN','GESTIONNAIRE')")
public class FormateurMeAffectationsController {

    private final UtilisateurRepository utilisateurRepo;
    private final AffectationRepository affectationRepo;

    public FormateurMeAffectationsController(UtilisateurRepository utilisateurRepo,
                                             AffectationRepository affectationRepo) {
        this.utilisateurRepo = utilisateurRepo;
        this.affectationRepo = affectationRepo;
    }

    /**
     * Sessions confirmées du formateur connecté (agenda).
     * Ex: /api/formateurs/me/affectations?from=2025-11-01T00:00:00&to=2026-11-01T00:00:00
     * Si absent: [now ; now + 12 mois]
     */
    @GetMapping("/affectations")
    public ResponseEntity<List<Session>> myConfirmedSessions(
            Authentication auth,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        String email = auth.getName(); // username = email
        Utilisateur u = utilisateurRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + email));

        if (u.getFormateur() == null || u.getFormateur().getIdFormateur() == null) {
            throw new EntityNotFoundException("Aucun profil formateur lié à l'utilisateur: " + email);
        }

        Integer formateurId = u.getFormateur().getIdFormateur();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime _from = (from != null) ? from : now.minusDays(1);
        LocalDateTime _to   = (to   != null) ? to   : now.plusMonths(12);

        List<Session> sessions =
                affectationRepo.findConfirmedSessionsByFormateurBetween(formateurId, _from, _to);

        return ResponseEntity.ok(sessions);
    }
}
