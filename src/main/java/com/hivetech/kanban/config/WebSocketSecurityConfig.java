package com.hivetech.kanban.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    /**
     * Defines who can subscribe/send on STOMP destinations.
     * The Builder is auto‐provided by Spring Security.
     */
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages
    ) {
        messages
                .simpSubscribeDestMatchers("/topic/tasks").authenticated()
                .simpDestMatchers("/app/**").authenticated()
                .anyMessage().denyAll();

        return messages.build();
    }
}
