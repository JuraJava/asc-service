package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import jakarta.persistence.*;
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
@Table(name = "work_order")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_repair_request", nullable = false)
    private RepairRequest repairRequest;

    @CreationTimestamp
    //    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "date_of_creation_work_order", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "repair_status", nullable = false)
    private RepairStatus repairStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "name_of_completed_work", nullable = false)
    private String completedWorkName;

    @Column(name = "batch_number_replaced_spare_part")
    private String batchNumber;

    @Column(name = "cost_of_spare_part")
    private BigDecimal costOfSparePart;

    @Column(name = "cost_of_work_performed", nullable = false)
    private BigDecimal costOfWork;

    @Column(name = "cost_of_additional_work_performed")
    private BigDecimal costOfAdditionalWork;

    @Column(name = "total_cost_of_work_order", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "salary_for_employee_on_this_order", nullable = false)
    private BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "id_of_employee_who_performed_repair", nullable = false)
    private Employee performedBy;

    @Column(name = "date_of_completion_of_repair")
    private LocalDateTime completedAt;
}
