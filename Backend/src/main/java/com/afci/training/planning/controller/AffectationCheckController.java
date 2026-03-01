package com.afci.training.planning.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.EligibilityResultDTO;
import com.afci.training.planning.service.AffectationCheckService;

/**
 * Contrôleur "utilitaire" pour vérifier l'éligibilité d'un formateur
 * sur un créneau donné. NE MODIFIE PAS la base.
 */
@RestController
@RequestMapping("/api/affectation")
public class AffectationCheckController {

    private final AffectationCheckService checkService;

    public AffectationCheckController(AffectationCheckService checkService) {
        this.checkService = checkService;
    }

    /**
     * Exemple :
     * GET /api/affectation/check?formateurId=1&from=2025-11-05T09:00:00&to=2025-11-06T18:00:00
     */
    @GetMapping("/check")
    public ResponseEntity<EligibilityResultDTO> check(
            @RequestParam Integer formateurId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        if (from == null || to == null) {
            throw new IllegalArgumentException("Paramètres 'from' et 'to' obligatoires (format ISO).");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("'to' doit être postérieur ou égal à 'from'.");
        }
        if (formateurId == null || formateurId <= 0) {
            throw new IllegalArgumentException("formateurId invalide.");
        }

        EligibilityResultDTO res = checkService.canAssign(formateurId, from, to);
        return ResponseEntity.ok(res);
    }
}
