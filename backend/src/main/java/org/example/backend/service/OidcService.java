package org.example.backend.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.Map;

@Service
public class OidcService {

    private final RestTemplate restTemplate = unsafeRestTemplate();

    public String exchangeCode(String code) {

        String tokenUrl = "https://localhost:10443/api/login/oauth/access_token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "bb9ce8c0a83b53d16285");
        body.add("client_secret", "cbc51354e8fdee0ca049f0dc3805240ee1a6a1d4");
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", "https://localhost:9000/callback");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> req =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp =
                restTemplate.postForEntity(tokenUrl, req, Map.class);

        return (String) resp.getBody().get("access_token");
    }

    private RestTemplate unsafeRestTemplate() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                        public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((a, b) -> true);

            return new RestTemplate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}