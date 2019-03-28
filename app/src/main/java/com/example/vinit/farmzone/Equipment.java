package com.example.vinit.farmzone;

public class Equipment
{
    private String provider_uid;
    private String type;
    private String description;
    private String price;
    private String status;
    private String equi_url;

    Equipment(){}

    public Equipment(String provider_uid, String type, String description, String price, String status, String equi_url) {
        this.provider_uid = provider_uid;
        this.type = type;
        this.description = description;
        this.price = price;
        this.status = status;
        this.equi_url = equi_url;
    }

    public String getProvider_uid() {
        return provider_uid;
    }

    public void setProvider_uid(String provider_uid) {
        this.provider_uid = provider_uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEqui_url() {
        return equi_url;
    }

    public void setEqui_url(String equi_url) {
        this.equi_url = equi_url;
    }


}
