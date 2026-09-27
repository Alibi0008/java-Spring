package com.example.practice_3.notify;

public interface NotificationSender {

    String channel();

    void send(String message);
}
