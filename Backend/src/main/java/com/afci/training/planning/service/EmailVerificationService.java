package com.afci.training.planning.service;

import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class EmailVerificationService {

    private final JwtUtil jwtUtil;
    private final UtilisateurRepository userRepo;

    /** Base URL publique de l’API (pour construire le lien de vérif) */
    private final String apiBaseUrl;
    /** Base URL du front (pour la redirection après vérif) */
    private final String frontBaseUrl;
    /** Durée d’expiration du lien en minutes (ex: 1440 = 24h) */
    private final int expMinutes;

    public EmailVerificationService(
            JwtUtil jwtUtil,
            UtilisateurRepository userRepo,
            @Value("${app.api.base-url:http://localhost:8080}") String apiBaseUrl,
            @Value("${app.front.base-url:http://localhost:5173}") String frontBaseUrl,
            @Value("${app.email.verify.exp-minutes:1440}") int expMinutes) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.apiBaseUrl = apiBaseUrl.replaceAll("/+$", "");
        this.frontBaseUrl = frontBaseUrl.replaceAll("/+$", "");
        this.expMinutes = expMinutes;
    }

    /** Génère un lien de vérification et (en DEV) le logge en console. */
    public String sendVerificationEmail(Utilisateur u) {
        Map<String, Object> claims = Map.of("sub", u.getEmail(), "type", "email-verify");
        String token = jwtUtil.generateTokenWithClaims(claims, expMinutes);
        String verifyUrl = apiBaseUrl + "/api/auth/email/verify?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        System.out.println("[EMAIL VERIFY] " + verifyUrl); // dev
        return verifyUrl; // <<<<<< IMPORTANT
    }

    /** Valide le token et marque l’email comme vérifié si tout est OK. */
    public boolean verifyTokenAndMarkEmail(String token) {
        try {
            Claims claims = jwtUtil.parseClaims(token);

            // Type de token attendu
            Object type = claims.get("type");
            if (!(type instanceof String) || !"email-verify".equals(type)) {
                return false;
            }

            // Expiration
            Date exp = claims.getExpiration();
            if (exp == null || exp.before(new Date())) {
                return false;
            }

            // Email → utilisateur
            String email = claims.getSubject();
            var opt = userRepo.findByEmail(email);
            if (opt.isEmpty()) return false;

            Utilisateur u = opt.get();
            if (!u.isEmailVerifie()) {
                u.setEmailVerifie(true);
                u.setUpdatedAt(Instant.now());
                userRepo.save(u);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /** URL front pour rediriger selon succès/échec, ex: /verify-email?status=success */
    public String frontRedirect(boolean success) {
        return frontBaseUrl + (success ? "/verify-email?status=success" : "/verify-email?status=error");
    }
}
