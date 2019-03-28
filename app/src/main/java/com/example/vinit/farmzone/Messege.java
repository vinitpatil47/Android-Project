package com.example.vinit.farmzone;

public class Messege
{
    private String from;
    private String message;
    private Long time;
    private String to;

    public Messege(){}

    public Messege(String from, String message, Long time, String to) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
