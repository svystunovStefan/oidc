package org.example.backend.JWT;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JwtVerifier {

    public static boolean verify(String token, RSAKey key) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            RSAPublicKey publicKey = key.toRSAPublicKey();

            boolean signatureValid =
                    jwt.verify(new RSASSAVerifier(publicKey));

            Date exp = jwt.getJWTClaimsSet().getExpirationTime();

            return signatureValid && exp != null && exp.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    // 🔥 helper (дуже важливо)
    public static RSAKey getKeyFromJwks(String kid) throws Exception {

        System.out.println("JWKS URL: " + JwkProvider.JWKS_URL);

        String json = new String(
                new java.net.URL(JwkProvider.JWKS_URL)
                        .openStream()
                        .readAllBytes()
        );

        // 🔥 ОЦЕ ТИ ДОДАЄШ ДЛЯ ДЕБАГУ
        System.out.println("JWKS RESPONSE:");
        System.out.println(json);

        JWKSet jwkSet = JWKSet.parse(json);

        for (JWK jwk : jwkSet.getKeys()) {
            if (jwk.getKeyID().equals(kid)) {
                return (RSAKey) jwk;
            }
        }

        throw new RuntimeException("Key not found: " + kid);
    }
}