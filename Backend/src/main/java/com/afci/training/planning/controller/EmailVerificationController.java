// src/main/java/com/afci/training/planning/controller/EmailVerificationController.java
package com.afci.training.planning.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.service.EmailVerificationService;
import com.afci.training.planning.service.security.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    private final EmailVerificationService emailSvc;
    private final AuthService authService;

    public EmailVerificationController(EmailVerificationService emailSvc, AuthService authService) {
        this.emailSvc = emailSvc;
        this.authService = authService;
    }

    /** Envoie (logge) le lien pour l’utilisateur courant */
    @PostMapping("/verify/send")
    public ResponseEntity<?> sendVerificationLink() {
        Utilisateur me = authService.getCurrentUser();
        String verifyUrl = emailSvc.sendVerificationEmail(me);
        return ResponseEntity.ok(java.util.Map.of("verifyUrl", verifyUrl)); // <<<<<< IMPORTANT
    }
    /** Valide le token et redirige vers le front */
    @GetMapping("/verify")
    public void verify(@RequestParam("token") String token, HttpServletResponse resp) throws IOException {
        boolean ok = emailSvc.verifyTokenAndMarkEmail(token);
        resp.sendRedirect(emailSvc.frontRedirect(ok));
    }
}
