package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Класс — это DTO, который используется для входящего запроса на создание платёжной транзакции в системе  через API.
 * Обычно его получает контроллер от клиента (UI или другой сервис).
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionsRequestDto {

    @NotNull(message = "Repair request id is required")
    private Long repairRequestId;

    @NotNull(message = "Type of payment is required")
    private TypeOfPayment typeOfPayment;

    @NotNull(message = "Currency is required")
    private Currencies currency;

    @NotNull(message = "Accepted by id is required")
    private Long acceptedById;;

}
