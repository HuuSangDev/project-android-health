package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class DeviceTokenRequest {

    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;

    public DeviceTokenRequest() {}

    public DeviceTokenRequest(String token, String platform) {
        this.token = token;
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
