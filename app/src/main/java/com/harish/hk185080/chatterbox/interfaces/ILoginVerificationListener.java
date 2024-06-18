package com.harish.hk185080.chatterbox.interfaces;

public interface ILoginVerificationListener {
    void onLoginVerificationSuccess();
    void onUserEmailNotVerified();
    void onLoginVerificationFailure();
}