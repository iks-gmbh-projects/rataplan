package de.iks.rataplan.websocket;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final List<ChannelRedirect> channelRedirects;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String command = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        Jwt token = command == null ? null : jwtDecoder.decode(command);
        switch(Objects.requireNonNullElse(accessor.getMessageType(), SimpMessageType.OTHER)) {
            case SUBSCRIBE:
                for(ChannelRedirect redirect : channelRedirects) {
                    if(redirect.matches(accessor.getDestination())) {
                        accessor.setDestination(redirect.redirect(accessor.getDestination(), token));
                        accessor.setLeaveMutable(true);
                        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                    }
                }
                throw new MessagingException("Invalid channel");
            case CONNECT:
            case DISCONNECT:
            case UNSUBSCRIBE:
            case HEARTBEAT:
            case MESSAGE:
                return message;
            default:
                throw new MessagingException("Invalid type");
        }
    }
}
