package com.github.kosurov.nasabot.services;

import com.github.kosurov.nasabot.models.IamTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class IamTokenService {

    @Value("${service.account.id}")
    private String serviceAccountId;

    @Value("${key.id}")
    private String keyId;

    private IamTokenResponse iamTokenResponse;

    public String requestJWTToken() throws Exception {
        PemObject privateKeyPem;
        try (PemReader reader = new PemReader(new FileReader("privateKey.txt"))) {
            privateKeyPem = reader.readPemObject();
        }

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyPem.getContent()));

        Instant now = Instant.now();

        // Формирование JWT.
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setIssuer(serviceAccountId)
                .setAudience("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(360)))
                .signWith(privateKey, SignatureAlgorithm.PS256)
                .compact();
    }

    public IamTokenResponse requestIamToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
        String jwt = requestJWTToken();

        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("jwt", jwt);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonData);
        return restTemplate.postForObject(url, request, IamTokenResponse.class);
    }

    @Scheduled(fixedRateString = "${token.update.period}")
    public void updateIamToken() {
        try {
            this.iamTokenResponse = requestIamToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IamTokenResponse getIamTokenResponse() {
        if (iamTokenResponse == null) {
            updateIamToken();
        }
        return iamTokenResponse;
    }
}
