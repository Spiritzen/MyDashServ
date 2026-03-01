package com.afci.training.planning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS (tu gardes ce que tu as)
    @Bean
    public CorsFilter corsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var cfg = new CorsConfiguration().applyPermitDefaultValues();
        cfg.addAllowedOriginPattern("http://localhost:5173");
        cfg.addAllowedMethod("*");
        cfg.addAllowedHeader("*");
        cfg.setAllowCredentials(false);
        source.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(source);
    }

    // ➜ Expose /files/** vers ton afci.upload.root
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // lis la propriété d’environnement (sans @ConfigurationProperties)
        String root = System.getProperty("afci.upload.root",
                        System.getenv().getOrDefault("AFCI_UPLOAD_ROOT", "D:/afci-uploads"));
        root = root.replace("\\", "/");
        if (!root.endsWith("/")) root += "/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + root);
    }
}
