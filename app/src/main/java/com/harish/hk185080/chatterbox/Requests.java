package com.harish.hk185080.chatterbox;

public class Requests {
    public String request_type;

    public Requests()
    {

    }

    public Requests(String request_type) {
        this.request_type = request_type;
    }

    public String getDate() {
        return request_type;
    }

    public void setDate(String request_type) {
        this.request_type = request_type;
    }
}
