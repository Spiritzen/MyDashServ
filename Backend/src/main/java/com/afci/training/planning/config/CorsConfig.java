package com.afci.training.planning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
  private String allowedOriginsCsv;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOriginsCsv.replace(" ", "").split(",");

        // Autorise toutes les routes API (ajoute d'autres patterns si besoin)
        registry.addMapping("/api/**")
            .allowedOrigins(origins)
            .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Location") // utile pour les POST/201
            .allowCredentials(true)
            .maxAge(3600); // cache du preflight (1h)
      }
    };
  }
}
