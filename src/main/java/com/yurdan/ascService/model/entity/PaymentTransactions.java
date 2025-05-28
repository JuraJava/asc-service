package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//@Entity
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

    @Column(name = "type_of_payment", nullable = false)
    private TypeOfPayment typeOfPayment;

    @Column(name = "currency", nullable = false)
    private Currencies currency;

    @CreationTimestamp
//    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

//    @Column(name = "id_of_employee_who_accepted_payment", nullable = false)
//    private Long idOfEmployeeWhoAcceptedPayment;
    @ManyToOne
    @JoinColumn(name = "id_of_employee_who_accepted_payment")
    private Employee idOfEmployeeWhoAcceptedPayment;

}
