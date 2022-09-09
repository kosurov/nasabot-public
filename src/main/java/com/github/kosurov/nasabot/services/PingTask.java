package com.github.kosurov.nasabot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
            URL pingTaskUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) pingTaskUrl.openConnection();
            connection.connect();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}