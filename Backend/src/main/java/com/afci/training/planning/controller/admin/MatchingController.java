package com.afci.training.planning.controller.admin;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.CandidateDTO;
import com.afci.training.planning.dto.PropositionRequest;
import com.afci.training.planning.service.MatchingService;
import com.afci.training.planning.service.AffectationService;

@RestController
@RequestMapping("/api/admin/sessions")
public class MatchingController {

    private final MatchingService matchingService;
    private final AffectationService affectationService;

    public MatchingController(MatchingService matchingService, AffectationService affectationService) {
        this.matchingService = matchingService;
        this.affectationService = affectationService;
    }

    @GetMapping("/{id}/candidates")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public List<CandidateDTO> candidates(@PathVariable Integer id,
                                         @RequestParam(defaultValue = "10") int limit) {
        return matchingService.findCandidates(id, limit);
    }

    @PostMapping("/{id}/propositions")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public void proposer(@PathVariable Integer id, @RequestBody PropositionRequest req) {
        affectationService.proposer(id, req.getFormateurId(), req.getNote());
    }
}
