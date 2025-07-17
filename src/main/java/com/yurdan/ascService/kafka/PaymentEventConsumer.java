package com.yurdan.ascService.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


/**
 * Класс реализует консьюмера (потребителя) Kafka, который
 * автоматически запускается вместе с Spring приложением,
 * подписан на определённый Kafka-топик,
 * получает сообщения в виде строк (JSON, текст),
 * логирует каждое сообщение в консоль или файл.
 */
@Slf4j
@Component
public class PaymentEventConsumer {
    @KafkaListener(topics = "payment-transactions-topic", groupId = "asc-group")
    public void handlePaymentEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);
    }
}