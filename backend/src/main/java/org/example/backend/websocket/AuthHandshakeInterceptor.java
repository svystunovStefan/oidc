package org.example.backend.websocket;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.JWT.JwtVerifier;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor
        implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        try {
            if (!(request instanceof ServletServerHttpRequest servletRequest)) {
                return false;
            }

            HttpServletRequest req = servletRequest.getServletRequest();

            if (req.getCookies() == null) return false;

            String token = null;

            for (Cookie c : req.getCookies()) {
                if ("access_token".equals(c.getName())) {
                    token = c.getValue();
                }
            }

            if (token == null) return false;

            SignedJWT jwt = SignedJWT.parse(token);

            String kid = jwt.getHeader().getKeyID();

            RSAKey key = JwtVerifier.getKeyFromJwks(kid);

            boolean valid = JwtVerifier.verify(token, key);

            // 🔥 додатково (ВАЖЛИВО ДЛЯ ЛАБИ)
            if (!valid) return false;

            attributes.put("user", jwt.getJWTClaimsSet().getSubject());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}