package com.yurdan.ascService.dto;

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
 * Класс — это DTO, предназначенный для ответа клиенту после выполнения платёжной транзакции.
 * Он передаётся из сервиса/контроллера в JSON-ответе клиенту.
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionsResponseDto {
    private Long id;
    private LocalDateTime executedAt;
    private TypeOfPayment typeOfPayment;
    private PaymentTransactionStatus transactionStatus;
    private Currencies currency;
    private BigDecimal amount;
    private Long acceptedBy;
    private Long repairRequestId;
}
