// src/main/java/com/afci/training/planning/controller/CandidatureController.java
package com.afci.training.planning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.afci.training.planning.dto.CandidatureStatusDTO;
import com.afci.training.planning.dto.CandidatureSubmitDTO;
import com.afci.training.planning.service.CandidatureService;
import com.afci.training.planning.service.security.AuthService; // adapte à ton projet

@RestController
@RequestMapping("/api/formateurs/me/candidature")
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final AuthService auth;

    public CandidatureController(CandidatureService candidatureService, AuthService auth) {
        this.candidatureService = candidatureService;
        this.auth = auth;
    }

    @GetMapping
    public CandidatureStatusDTO getStatus() {
        return candidatureService.getStatus(auth.getCurrentUserId());
    }

    @PutMapping("/soumettre")
    public CandidatureStatusDTO soumettre(@RequestBody CandidatureSubmitDTO payload) {
        return candidatureService.soumettre(auth.getCurrentUserId(), payload);
    }

    @PutMapping("/annuler")
    public CandidatureStatusDTO annuler() {
        return candidatureService.annulerSoumission(auth.getCurrentUserId());
    }
}
