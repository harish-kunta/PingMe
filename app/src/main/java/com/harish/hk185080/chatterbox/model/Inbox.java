package com.harish.hk185080.chatterbox.model;

import android.graphics.drawable.Drawable;

public class Inbox {

    public boolean seen;
    public long timestamp;

    public Inbox() {
    }

    public Inbox(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}