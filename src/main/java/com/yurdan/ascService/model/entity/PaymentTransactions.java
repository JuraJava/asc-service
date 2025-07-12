package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.PaymentTransactionStatus;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "payment_transactions")
public class PaymentTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_payment", nullable = false)
    private TypeOfPayment typeOfPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_transaction_status", nullable = false)
    private PaymentTransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currencies currency;

    @CreationTimestamp
    //    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @OneToOne
    @JoinColumn(name = "id_repair_request", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne
    @JoinColumn(name = "id_employee_who_accepted_payment", nullable = false)
    private Employee acceptedBy;

    @Builder
    public PaymentTransactions(Long id,
                               BigDecimal amount,
                               TypeOfPayment typeOfPayment,
                               PaymentTransactionStatus transactionStatus,
                               Currencies currency,
                               LocalDateTime executedAt,
                               RepairRequest repairRequest,
                               Employee acceptedBy) {
        this.id = id;
        this.amount = amount;
        this.typeOfPayment = typeOfPayment;
        this.transactionStatus = transactionStatus;
        this.currency = currency;
        this.executedAt = executedAt;
        this.repairRequest = repairRequest;
        this.acceptedBy = acceptedBy;
    }
}


