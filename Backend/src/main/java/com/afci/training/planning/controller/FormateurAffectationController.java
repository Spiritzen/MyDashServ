package com.afci.training.planning.controller;

import com.afci.training.planning.service.FormateurAffectationService;
import com.afci.training.planning.repository.projection.FormateurAffectationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/formateur/affectations")
@PreAuthorize("hasRole('FORMATEUR')")
public class FormateurAffectationController {

    private final FormateurAffectationService service;

    public FormateurAffectationController(FormateurAffectationService service) {
        this.service = service;
    }

    // GET /api/formateur/affectations?statut=PROPOSEE&page=0&size=10
    @GetMapping
    public Page<FormateurAffectationProjection> list(
            Principal principal,
            @RequestParam Optional<String> statut,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dateDebut"));
        return service.listForCurrentFormateur(principal.getName(), statut.orElse(null), pageable);
    }

    // POST /api/formateur/affectations/{id}/accept
    @PostMapping("/{id}/accept")
    public void accept(@PathVariable Integer id, Principal principal) {
        service.accept(principal.getName(), id);
    }

    // POST /api/formateur/affectations/{id}/refuse
    @PostMapping("/{id}/refuse")
    public void refuse(@PathVariable Integer id, Principal principal) {
        service.refuse(principal.getName(), id);
    }
}
