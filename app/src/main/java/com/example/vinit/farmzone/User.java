package com.example.vinit.farmzone;

public class User
{
    private String name;
    private String email_id;
    private String mobile_no;
    private String profile_url;
    private String device_token;
    private String latitude;
    private String longitude;

    public User(){}

    public User(String name, String email_id, String mobile_no, String profile_url, String device_token, String latitude, String longitude) {
        this.name = name;
        this.email_id = email_id;
        this.mobile_no = mobile_no;
        this.profile_url = profile_url;
        this.device_token = device_token;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
