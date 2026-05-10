package org.example.backend.controller;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private static final String JWKS_URL =
            "https://localhost:10443/.well-known/jwks";

    @GetMapping("/debug-cookie")
    public ResponseEntity<?> debugCookie(
            @CookieValue(value = "access_token", required = false) String token) {

        Map<String, Object> res = new HashMap<>();
        res.put("token_exists", token != null);
        res.put("token_preview", token == null ? null :
                token.substring(0, Math.min(30, token.length())));

        return ResponseEntity.ok(res);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> userInfo(HttpServletRequest request) {

        try {

            Cookie[] cookies = request.getCookies();

            if (cookies == null) {

                Map<String, Object> err = new HashMap<>();
                err.put("error", "NO COOKIES");

                return ResponseEntity.status(401).body(err);
            }

            String token = null;

            for (Cookie c : cookies) {

                if ("access_token".equals(c.getName())) {
                    token = c.getValue();
                }
            }

            if (token == null) {

                Map<String, Object> err = new HashMap<>();
                err.put("error", "TOKEN NOT FOUND");

                return ResponseEntity.status(401).body(err);
            }

            SignedJWT jwt = SignedJWT.parse(token);

            Map<String, Object> claims =
                    jwt.getJWTClaimsSet().getClaims();

            Map<String, Object> result = new HashMap<>();

            result.put("id", claims.get("sub"));
            result.put("name", claims.get("name"));
            result.put("email", claims.get("email"));
            result.put("admin", claims.get("isAdmin"));

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());

            return ResponseEntity.status(500).body(err);
        }
    }
}