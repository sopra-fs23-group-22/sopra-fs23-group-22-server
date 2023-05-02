package ch.uzh.ifi.hase.soprafs23.socket;

import com.sun.istack.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration
    @EnableWebSocketMessageBroker
public class Socket implements WebSocketMessageBrokerConfigurer {

        private static final String WEBSOCKET_PREFIX = "/topic";

        private static final String WEBSOCKET_SUFFIX = "/sopra-websocket";

        private static final String ORIGIN_LOCALHOST = "http://localhost:3000";

        private static final String ORIGIN_PROD = "sopra-fs23-group-22-client.oa.r.appspot.com";

        @Override
        public void configureMessageBroker(@NotNull MessageBrokerRegistry config) {

            config.enableSimpleBroker(WEBSOCKET_PREFIX);

        }

        @Override
        public void registerStompEndpoints(@NotNull StompEndpointRegistry registry) {

            registry.addEndpoint(WEBSOCKET_SUFFIX)
                    .setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD)
                    .withSockJS();
        }
    }
