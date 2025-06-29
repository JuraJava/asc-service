package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private TypeOfPayment type;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currencies currency;

    @CreationTimestamp
    //    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "id_employee_who_accepted_payment", nullable = false)
    private Employee acceptedBy;
}


