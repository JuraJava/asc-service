package com.yurdan.ascService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.kafkaDto.PaymentTransactionResponseKafkaDto;
import com.yurdan.ascService.model.entity.PaymentResult;
import com.yurdan.ascService.service.PaymentResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 	Этот класс получает JSON-сообщение из Kafka-топика payment-response-topic,
 * 	преобразует сообщение в DTO (PaymentTransactionResponseKafkaDto),
 * 	преобразует DTO в сущность PaymentResult,
 * 	сохраняет эту сущность в БД через сервис,
 * 	логирует шаги и обрабатывает исключения
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentResultService paymentResultService;

    @KafkaListener(topics = "payment-response-topic", groupId = "asc-service-response-group")
    public void listen(String message) {
        try {
            PaymentTransactionResponseKafkaDto dto =
                    objectMapper.readValue(message, PaymentTransactionResponseKafkaDto.class);

            log.info("Получено событие от PAYMENT-сервиса: {}", dto);

            PaymentResult result = PaymentResult.builder()
                    .paymentId(dto.getId())
                    .repairRequestId(dto.getRepairRequestId())
//                    .acceptedById(dto.getAcceptedById())
                    .acceptedById(dto.getAcceptedBy())
                    .amount(dto.getAmount())
                    .currency(dto.getCurrency())
                    .typeOfPayment(dto.getTypeOfPayment())
                    .transactionStatus(dto.getTransactionStatus())
                    .receivedAt(LocalDateTime.now())
                    .build();

            paymentResultService.savePaymentResult(result); // <--- теперь через сервис
            log.info("Сохранен результат обработки платежа в БД: ID = {}", result.getId());

        } catch (Exception e) {
            log.error("Ошибка обработки события из payment-response-topic", e);
        }
    }
}
