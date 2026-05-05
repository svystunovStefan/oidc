package org.example.backend.JWT;

import java.util.Map;

public class JwtClaimsUtil {

    public static String getEmail(Map<String, Object> claims) {
        return (String) claims.getOrDefault("email", "");
    }

    public static String getName(Map<String, Object> claims) {
        return (String) claims.getOrDefault("preferred_username",
                claims.getOrDefault("name", ""));
    }

    public static String getId(Map<String, Object> claims) {
        return (String) claims.getOrDefault("sub", "");
    }
}