package com.afci.training.planning.security;

import com.afci.training.planning.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository repo;

    @Autowired
    public CustomUserDetailsService(UtilisateurRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // u.getRole() est un enum: ADMIN / FORMATEUR / GESTIONNAIRE
        return User.withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .roles(u.getRole().name())  // ajoute automatiquement "ROLE_"
                .build();
    }
}
