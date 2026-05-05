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
            "https://localhost:10443/.well-known/jwks.json";

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

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return ResponseEntity.status(401).body("NO COOKIES AT ALL");
        }

        String token = null;

        for (Cookie c : cookies) {
            System.out.println("COOKIE: " + c.getName() + " = " + c.getValue());

            if ("access_token".equals(c.getName())) {
                token = c.getValue();
            }
        }

        if (token == null) {
            return ResponseEntity.status(401).body("ACCESS TOKEN NOT FOUND");
        }

        try {
            SignedJWT jwt = SignedJWT.parse(token);

            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

            Map<String, Object> result = new HashMap<>();
            result.put("id", claims.get("sub"));
            result.put("name", claims.get("name"));
            result.put("email", claims.get("email"));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("JWT ERROR: " + e.getMessage());
        }
    }
}