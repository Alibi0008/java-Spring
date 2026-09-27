package com.example.practice_3.notify;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryNotifier {

    private final List<NotificationSender> senders;

    public DeliveryNotifier(List<NotificationSender> senders) {
        this.senders = List.copyOf(senders);
    }

    public List<String> notifyAll(String message) {
        senders.forEach(sender -> sender.send(message));
        return channels();
    }

    public List<String> channels() {
        return senders.stream().map(NotificationSender::channel).toList();
    }
}
