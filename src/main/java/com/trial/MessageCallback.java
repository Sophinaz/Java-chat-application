package com.trial;

public interface MessageCallback {
    void onMessageSent(String message);
    void onDisconnect();
    void onReconnect();
}
