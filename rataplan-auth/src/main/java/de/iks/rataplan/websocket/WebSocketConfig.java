package de.iks.rataplan.websocket;

import de.iks.rataplan.config.FrontendConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final FrontendConfig frontendConfig;
    private final JwtInterceptor jwtInterceptor;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/");
        config.setUserDestinationPrefix("/user/");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications")
            .setAllowedOrigins(frontendConfig.getUrl());
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
    
    
    
    @Override
    public void configureClientInboundChannel(
        ChannelRegistration registration
    )
    {
        registration.interceptors(this.jwtInterceptor);
    }
    
    //    @Override
    //    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    //        messages.simpTypeMatchers(SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE, SimpMessageType
    //        .DISCONNECT)
    //            .permitAll() //or permitAll
    //            .simpSubscribeDestMatchers("/notifications/[0-9]+")
    //            .hasRole("USER")
    //            .anyMessage()
    //            .denyAll();
    //    }
    
    //    @Override
    //    protected boolean sameOriginDisabled() {
    //        return true;
    //    }
}