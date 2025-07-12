package com.yurdan.ascService.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventConsumer {
    @KafkaListener(topics = "payment-transactions-topic", groupId = "asc-group")
    public void handlePaymentEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);
    }
}