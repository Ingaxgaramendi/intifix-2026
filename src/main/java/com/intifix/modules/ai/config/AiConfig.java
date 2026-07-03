package com.intifix.modules.ai.config;

import com.intifix.modules.ai.prompt.SystemPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del {@link ChatClient} de Spring AI.
 *
 * <p>{@code ChatClient.Builder} es autoconfigurado por
 * {@code spring-ai-starter-model-openai} a partir de las propiedades
 * {@code spring.ai.openai.*}. Aquí solo fijamos el prompt de sistema por
 * defecto; las herramientas se registran por petición en el servicio para
 * poder inyectar contexto del usuario.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient intifixChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(SystemPrompts.CORE)
                .build();
    }
}
