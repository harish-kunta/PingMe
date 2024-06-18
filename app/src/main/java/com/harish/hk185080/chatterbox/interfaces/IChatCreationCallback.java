package com.harish.hk185080.chatterbox.interfaces;

public interface IChatCreationCallback {
    void onChatCreationSuccess(String chatId);

    void onChatCreationFailed(String errorMessage);
}
