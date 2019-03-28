package com.example.vinit.farmzone;

public class Uid {

    private String equi_key;
    private String uid;
    private String status;


    public Uid(){}

    public Uid(String equi_key, String uid, String status) {
        this.equi_key = equi_key;
        this.uid = uid;
        this.status = status;
    }

    public String getEqui_key() {
        return equi_key;
    }

    public void setEqui_key(String equi_key) {
        this.equi_key = equi_key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
