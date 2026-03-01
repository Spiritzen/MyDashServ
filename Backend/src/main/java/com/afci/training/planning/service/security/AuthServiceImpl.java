// src/main/java/com/afci/training/planning/service/security/AuthServiceImpl.java
package com.afci.training.planning.service.security;

import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UtilisateurService utilisateurService;

    public AuthServiceImpl(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public Integer getCurrentUserId() {
        Utilisateur u = getCurrentUser();
        if (u == null) throw new IllegalStateException("Utilisateur non authentifié.");
        return u.getIdUtilisateur(); // adapte au nom exact de ta PK
    }

    @Override
    public String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Utilisateur non authentifié.");
        }
        return auth.getName(); // par défaut : username = email via UserDetails
    }

    @Override
    public Utilisateur getCurrentUser() {
        String email = getCurrentEmail();
        return utilisateurService.findByEmailOrThrow(email);
    }
}
