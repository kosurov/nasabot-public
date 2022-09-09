package com.github.kosurov.nasabot.services;

import com.github.kosurov.nasabot.models.YandexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslateService {
    private final IamTokenService iamTokenService;
    private final String folderId;
    @Autowired
    public TranslateService(IamTokenService iamTokenService, @Value("${yandex.folder.id}") String folderId) {
        this.iamTokenService = iamTokenService;
        this.folderId = folderId;
    }

    public YandexResponse Translate(String textToTranslate) {
        String token = iamTokenService.getIamTokenResponse().getIamToken();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://translate.api.cloud.yandex.net/translate/v2/translate";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Bearer " + token);

        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("folderId", folderId);
        jsonData.put("targetLanguageCode", "ru");
        jsonData.put("texts", textToTranslate);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonData, httpHeaders);
        return restTemplate.postForObject(url, request, YandexResponse.class);
    }
}
