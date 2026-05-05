package org.example.backend.JWT;

import java.net.URL;

public class JwkProvider {

    public static final String JWKS_URL =
            "https://localhost:10443/.well-known/jwks.json";

    public static URL getJwksUrl() throws Exception {
        return new URL(JWKS_URL);
    }
}