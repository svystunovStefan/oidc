package org.example.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.JWT.*;
import org.example.backend.service.OidcService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final OidcService oidcService;

    public AuthController(OidcService oidcService) {
        this.oidcService = oidcService;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {

        String url =
                "https://localhost:10443/login/oauth/authorize" +  // ✅ ОЦЕ СЮДИ FIX
                        "?client_id=bb9ce8c0a83b53d16285" +
                        "&response_type=code" +
                        "&redirect_uri=https://localhost:9000/callback" +
                        "&scope=openid profile email";

        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         HttpServletResponse response) throws IOException {

        try {
            String token = oidcService.exchangeCode(code);

            Cookie cookie = new Cookie("access_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);

            response.addHeader("Set-Cookie",
                    "access_token=" + token +
                            "; Path=/" +
                            "; HttpOnly" +
                            "; Secure" +
                            "; SameSite=None"
            );

            response.addCookie(cookie);

            response.sendRedirect("/index.html?login=success");

        } catch (Exception e) {
            response.sendRedirect("/index.html?login=failed");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("access_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}