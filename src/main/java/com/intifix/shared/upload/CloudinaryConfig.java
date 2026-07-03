package com.intifix.shared.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Crea el cliente {@link Cloudinary} SOLO cuando {@code app.cloudinary.cloud-name}
 * tiene valor (se rellena desde el {@code .env}). Si no está configurado, el bean
 * no se crea y {@link FileUploadController} cae al almacenamiento local.
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${app.cloudinary.cloud-name:}' != ''")
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${app.cloudinary.cloud-name}") String cloudName,
            @Value("${app.cloudinary.api-key}") String apiKey,
            @Value("${app.cloudinary.api-secret}") String apiSecret) {

        log.info("Cloudinary habilitado (cloud-name='{}'); las imágenes se subirán a la nube.", cloudName);
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }
}
