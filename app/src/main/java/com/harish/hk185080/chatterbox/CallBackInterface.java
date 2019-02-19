package com.harish.hk185080.chatterbox;

import com.harish.hk185080.chatterbox.model.Inbox;

import java.util.List;

public interface CallBackInterface {
    public abstract void jobDone(List<Inbox>data,List<String> uidGroup);
}
