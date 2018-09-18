package com.harish.hk185080.chatterbox;

public class Messages {
    public String message,type;
    public long time;
    public boolean seen;
    public String from;
    public String messageId;

    public Messages(String message, String type, long time, boolean seen, String from,String messageId) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
        this.messageId=messageId;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Messages()
    {

    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
