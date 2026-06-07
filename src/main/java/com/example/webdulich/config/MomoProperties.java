package com.example.webdulich.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {

    private String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType = "captureWallet";
    private String lang = "vi";

    // true = dùng trang MoMo giả lập trong project
    // false = gọi API MoMo test/thật
    private boolean demoMode = false;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isDemoMode() {
        return demoMode;
    }

    public void setDemoMode(boolean demoMode) {
        this.demoMode = demoMode;
    }

    public boolean isConfigured() {
        return hasText(endpoint)
                && hasText(partnerCode)
                && hasText(accessKey)
                && hasText(secretKey)
                && hasText(redirectUrl)
                && hasText(ipnUrl)
                && !partnerCode.contains("YOUR_")
                && !accessKey.contains("YOUR_")
                && !secretKey.contains("YOUR_");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}