package com.yurdan.ascService.scheduler;

import com.yurdan.ascService.model.entity.OutboxEvent;
import com.yurdan.ascService.model.enums.OutboxEventStatus;
import com.yurdan.ascService.repository.CompletedWorkRepository;
import com.yurdan.ascService.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Это класс планировщик задач, который периодически читает события из таблицы Outbox (в статусе READY),
 * отправляет их в Kafka, обновляет статус события на SENT после успешной отправки.
 * Это типичная реализация "Outbox Pattern" — шаблона надёжной доставки событий из БД в брокер сообщений (Kafka).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Если Kafka-топик фиксирован, можно хардкодить:
    @Value("${kafka.payment.topic-in}")
    private String TOPIC;

    private final CompletedWorkRepository completedWorkRepository;

    /**
     * Этот метод выполняется каждые fixedDelayString миллисекунд, где fixedDelayString берется из application.yaml
     */
    @Scheduled(fixedDelayString = "${scheduler.outbox.fixed-delay-ms}")
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus.READY);

        if (events.isEmpty()) {
            log.info("No outbox events to publish.");
            return;
        }

        log.info("Publishing {} outbox events to Kafka...", events.size());

        for (OutboxEvent event : events) {
            try {
                // Создание ProducerRecord с headers
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, event.getAggregateId(), event.getPayload());

                // Добавляем заголовки Kafka
                if (event.getEventType() != null) {
                    record.headers().add("eventType", event.getEventType().name().getBytes(StandardCharsets.UTF_8));
                }
                record.headers().add("aggregateType", event.getAggregateType().getBytes());
                record.headers().add("createdAt", event.getCreatedAt().toString().getBytes());

                // Логирование перед отправкой
                log.debug("Sending Kafka record: topic={}, key={}, value={}, headers={}",
                        record.topic(), record.key(), record.value(), record.headers());

                // Отправка через KafkaTemplate
                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);

                future.whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event id={} to Kafka: {}", event.getId(), ex.getMessage(), ex);
                        // Можно обновить статус на ERROR при необходимости
                    } else {
                        event.setStatus(OutboxEventStatus.SENT);
                        outboxEventRepository.save(event);

                        log.info("Successfully published event id={} to topic={}, partition={}, offset={}",
                                event.getId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });

            } catch (Exception e) {
                log.error("Unexpected error while sending event id={} to Kafka", event.getId(), e);
                // Можно обновить статус на ERROR при необходимости
            }
        }
    }
}