package com.yurdan.ascService.dto.kafkaDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.PaymentTransactionStatus;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс представляет собой DTO, предназначенный для передачи данных о платёжной транзакции через Kafka
 * или другие внешние системы, содержит информацию о платеже по заявке на ремонт.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentTransactionResponseKafkaDto {
    private Long id;
    private PaymentTransactionStatus transactionStatus;
    private TypeOfPayment typeOfPayment;
    private Currencies currency;
    private BigDecimal amount;
    private Long repairRequestId;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
//    private Long acceptedById;
    private Long acceptedBy;
}