package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.CompletedWork;
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
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_repair_request", nullable = false)
    private Long idRepairRequest;

    @CreationTimestamp
//    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "date_of_creation_work_order", nullable = false)
    private LocalDateTime dateOfCreationWorkOrder;

    @Column(name = "repair_status", nullable = false)
    private RepairStatus repairStatus;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "completed_work", nullable = false)
    private CompletedWork completedWork;

    @Column(name = "replaced_spare_parts")
    private String replacedSpareParts;

    @Column(name = "cost_of_spare_all_parts")
    private BigDecimal costOfSpareAllParts;

    @Column(name = "cost_of_work_performed", nullable = false)
    private BigDecimal costOfWorkPerformed;

    @Column(name = "cost_of_additional_work_performed")
    private BigDecimal costOfAdditionalWorkPerformed;

    @Column(name = "total_cost_of_work_order", nullable = false)
    private BigDecimal totalCostOfWorkOrder;

    @Column(name = "salary_for_employee_on_this_order", nullable = false)
    private BigDecimal salaryForEmployeeOnThisOrder;

    @Column(name = "id_of_employee_who_performed_repair", nullable = false)
    private Long idOfEmployeeWhoPerformedRepair;

    @Column(name = "date_of_completion_of_repairs")
    private LocalDateTime dateOfCompletionOfRepairs;

}
