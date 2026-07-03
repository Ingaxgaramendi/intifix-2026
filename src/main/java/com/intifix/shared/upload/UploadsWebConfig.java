package com.intifix.shared.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sirve los archivos subidos localmente bajo {@code /uploads/**} desde la
 * carpeta {@code app.uploads.dir}.
 */
@Slf4j
@Configuration
public class UploadsWebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(uploadsDir).toAbsolutePath().normalize();
        try {
            // Crear la carpeta al arrancar para que la ubicación del recurso
            // resuelva como directorio (con barra final).
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.warn("No se pudo crear la carpeta de uploads {}: {}", dir, e.getMessage());
        }

        String location = dir.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
        log.info("Sirviendo /uploads/** desde {}", location);
    }
}
