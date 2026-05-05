package org.example.backend.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtDecoder {

    public static Map<String, Object> decode(String jwt) {
        try {
            if (jwt == null || jwt.split("\\.").length < 2) {
                throw new RuntimeException("Invalid JWT format");
            }

            String payload = jwt.split("\\.")[1];

            String json = new String(
                    Base64.getUrlDecoder().decode(payload)
            );

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("JWT decode error", e);
        }
    }
}