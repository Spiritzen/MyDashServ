package com.afci.training.planning.controller.formateur;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.RespondRequest;
import com.afci.training.planning.service.AffectationService;

@RestController
@RequestMapping("/api/formateur/affectations")
public class AffectationController {

    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasRole('FORMATEUR')")
    public void respond(@PathVariable Integer id, @RequestBody RespondRequest req) {
        affectationService.repondre(id, req.isAccept());
    }
}
