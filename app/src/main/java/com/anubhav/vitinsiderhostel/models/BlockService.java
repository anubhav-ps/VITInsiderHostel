package com.anubhav.vitinsiderhostel.models;

public class BlockService {
    private int imageUrl;
    private String serviceName;

    public BlockService() {
    }

    public BlockService(int imageUrl, String serviceName) {
        this.imageUrl = imageUrl;
        this.serviceName = serviceName;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
