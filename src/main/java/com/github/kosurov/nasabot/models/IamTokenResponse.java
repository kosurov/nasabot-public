package com.github.kosurov.nasabot.models;

public class IamTokenResponse {
    private String iamToken;
    private String expiresAt;

    public String getIamToken() {
        return iamToken;
    }

    public void setIamToken(String iamToken) {
        this.iamToken = iamToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
