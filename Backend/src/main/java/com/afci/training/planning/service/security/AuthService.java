// src/main/java/com/afci/training/planning/service/security/AuthService.java
package com.afci.training.planning.service.security;

import com.afci.training.planning.entity.Utilisateur;

public interface AuthService {
    Integer getCurrentUserId();
    String getCurrentEmail();
    Utilisateur getCurrentUser(); // pratique si on veut tout l'objet
}
