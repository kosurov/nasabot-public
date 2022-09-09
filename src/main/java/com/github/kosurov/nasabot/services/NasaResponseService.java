package com.github.kosurov.nasabot.services;

import com.github.kosurov.nasabot.models.NasaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NasaResponseService {

    @Value("${nasa.apikey}")
    private String apiKey;

    public NasaResponse nasaRequest() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;
        System.out.println(url);
        return restTemplate.getForObject(url, NasaResponse.class);
    }
}
