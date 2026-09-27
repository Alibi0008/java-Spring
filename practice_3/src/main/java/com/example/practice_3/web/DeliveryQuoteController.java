package com.example.practice_3.web;

import com.example.practice_3.delivery.DeliveryQuote;
import com.example.practice_3.delivery.DeliveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryQuoteController {

    private final DeliveryService deliveryService;

    public DeliveryQuoteController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/quote")
    public DeliveryQuote quote(
            @RequestParam String region,
            @RequestParam long amount
    ) {
        return deliveryService.quote(region, amount);
    }
}
