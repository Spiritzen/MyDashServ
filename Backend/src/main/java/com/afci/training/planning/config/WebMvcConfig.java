package com.afci.training.planning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadsRoot;

    public WebMvcConfig(@Value("${afci.upload.root}") String uploadsRoot) {
        this.uploadsRoot = uploadsRoot;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose /files/** en lecture depuis le dossier uploadsRoot (en dehors du classpath)
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadsRoot + "/")
                .setCachePeriod(3600); // 1h (on fera du cache-busting côté front avec ?v=timestamp)
    }
}
