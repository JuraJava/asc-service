package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.PaymentTransactionStatus;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_result")
@Getter
@Setter
@NoArgsConstructor
public class PaymentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "repair_request_id", nullable = false)
    private Long repairRequestId;

    @Column(name = "accepted_by_id", nullable = false)
    private Long acceptedById;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currencies currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_payment", nullable = false)
    private TypeOfPayment typeOfPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private PaymentTransactionStatus transactionStatus;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Builder
    public PaymentResult(Long id,
                         Long paymentId,
                         Long repairRequestId,
                         Long acceptedById,
                         BigDecimal amount,
                         Currencies currency,
                         TypeOfPayment typeOfPayment,
                         PaymentTransactionStatus transactionStatus,
                         LocalDateTime receivedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.repairRequestId = repairRequestId;
        this.acceptedById = acceptedById;
        this.amount = amount;
        this.currency = currency;
        this.typeOfPayment = typeOfPayment;
        this.transactionStatus = transactionStatus;
        this.receivedAt = receivedAt;
    }
}