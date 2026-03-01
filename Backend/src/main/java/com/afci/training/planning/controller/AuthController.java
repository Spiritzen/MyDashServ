package com.afci.training.planning.controller;

import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UtilisateurRepository repo;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UtilisateurRepository repo) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.repo = repo;
    }

    // ----- LOGIN -----
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var u = repo.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

        Map<String, Object> user = new HashMap<>();
        // user.put("id", u.getIdUtilisateur()); 
        user.put("email", u.getEmail());
        String fullName = ((u.getPrenom() != null ? u.getPrenom() + " " : "")
                + (u.getNom() != null ? u.getNom() : "")).trim();
        user.put("fullName", fullName);
        user.put("roles", List.of("ROLE_" + u.getRole().name()));

        return ResponseEntity.ok(Map.of("token", token, "user", user));
    }

    // ----- /me : infos de la session courante (protégée) -----
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        String email = auth.getName(); // même chose que ((User)auth.getPrincipal()).getUsername()
        var u = repo.findByEmail(email).orElseThrow();

        String fullName = ((u.getPrenom() != null ? u.getPrenom() + " " : "")
                + (u.getNom() != null ? u.getNom() : "")).trim();

        return Map.of(
                "email", u.getEmail(),
                "fullName", fullName,
                "roles", List.of("ROLE_" + u.getRole().name())
        );
    }

    // ----- DTO -----
    public static class LoginRequest {
        private String email;
        private String password;
        public LoginRequest() {}
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
