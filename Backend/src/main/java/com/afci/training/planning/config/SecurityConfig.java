package com.afci.training.planning.config;

import com.afci.training.planning.security.CustomUserDetailsService;
import com.afci.training.planning.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 🔐 Configuration principale Spring Security
 * - Stateless (JWT)
 * - Sécurise toutes les routes REST
 * - Définit les périmètres selon le rôle (ADMIN, GESTIONNAIRE, FORMATEUR)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ======== Configuration stateless et CORS ========
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ======== AUTORISATIONS PAR ROUTE ========
            .authorizeHttpRequests(reg -> reg

                // --- PUBLIC GÉNÉRIQUE ---
                .requestMatchers("/api/health", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // --- FICHIERS STATIQUES (photos, etc.) ---
                .requestMatchers(HttpMethod.GET, "/files/**").permitAll()

                // --- CATALOGUE PUBLIC (visiteurs) ---
                .requestMatchers(HttpMethod.GET, "/api/themes").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/competences", "/api/competences/**").permitAll()

                // --- VISUALISATION PHOTO USER ---
                .requestMatchers(HttpMethod.GET, "/api/users/*/photo").permitAll()

                // --- INSCRIPTION ---
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                // --- EMAIL VERIFY (public pour lien de confirmation) ---
                .requestMatchers(HttpMethod.POST, "/api/auth/email/verify/send").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/auth/email/verify").permitAll()

                // --- ESPACE PERSONNEL UTILISATEUR ---
                .requestMatchers("/api/me/**").authenticated()
                .requestMatchers(HttpMethod.GET,    "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.PUT,    "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.POST,   "/api/users/me/photo").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/users/me/photo").authenticated()

                // --- PROFIL FORMATEUR ---
                .requestMatchers(HttpMethod.GET, "/api/formateurs/me").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/formateurs/me").authenticated()

                // --- CANDIDATURE FORMATEUR ---
                .requestMatchers(HttpMethod.GET,  "/api/formateurs/me/candidature").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/api/formateurs/me/candidature/soumettre").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/api/formateurs/me/candidature/annuler").authenticated()

                // --- AGENDA FORMATEUR ---
                .requestMatchers(HttpMethod.GET, "/api/formateurs/*/agenda")
                    .hasAnyRole("ADMIN","GESTIONNAIRE","FORMATEUR")

                // --- MES AFFECTATIONS (formateur connecté) ---
                .requestMatchers(HttpMethod.GET, "/api/formateurs/me/affectations")
                    .hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")

                // --- CONGÉS ---
                .requestMatchers(HttpMethod.POST,   "/api/conges").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.PUT,    "/api/conges/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.DELETE, "/api/conges/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.GET,    "/api/conges/formateur/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")

                // --- DISPONIBILITÉS ---
                .requestMatchers(HttpMethod.POST,   "/api/disponibilites").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.PUT,    "/api/disponibilites/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.DELETE, "/api/disponibilites/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.GET,    "/api/disponibilites/formateur/*").hasAnyRole("FORMATEUR","ADMIN","GESTIONNAIRE")

                // --- AFFECTATIONS & MATCHING ---
                .requestMatchers(HttpMethod.GET,  "/api/affectation/check").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.POST, "/api/affectation/assign").hasAnyRole("ADMIN","GESTIONNAIRE")

                // ✅ NOUVEAUX ENDPOINTS (matching et consultation)
                .requestMatchers(HttpMethod.GET,  "/api/affectation/candidates").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers(HttpMethod.GET,  "/api/affectation/session/*").hasAnyRole("ADMIN","GESTIONNAIRE")

                // --- FORMATEURS (gestion globale ADMIN / GESTIONNAIRE) ---
                .requestMatchers("/api/formateurs/**").hasAnyRole("ADMIN","GESTIONNAIRE")

                // --- ZONES ADMIN ---
                .requestMatchers("/api/admin/sessions", "/api/admin/sessions/**").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers("/api/admin/formations", "/api/admin/formations/**").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers("/api/admin/formateurs", "/api/admin/formateurs/**").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers("/api/admin/evidences/**").hasAnyRole("ADMIN","GESTIONNAIRE")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // --- SWAGGER (optionnel, dev only) ---
                .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()

                // --- TOUT LE RESTE = AUTH REQUISE ---
                .anyRequest().authenticated()
            )

            // ======== UserDetails & Filter JWT ========
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Fournit le manager d’authentification à utiliser dans AuthController */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
