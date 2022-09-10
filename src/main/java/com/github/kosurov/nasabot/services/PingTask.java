package com.github.kosurov.nasabot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Special app invoker for Heroku free plan.
 * Provides app not to sleep after 30 min inactive.
 */
@Service
public class PingTask {
    @Value("${pingtask.url}")
    private String url;

    @Scheduled(fixedRateString = "${pingtask.period}")
    public void pingMe() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            System.out.println(url);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            System.out.println(e.getStatusCode());
        }
    }
}