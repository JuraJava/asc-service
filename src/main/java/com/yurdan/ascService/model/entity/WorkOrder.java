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

//@Entity
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

//    @Column(name = "id_repair_request", nullable = false)
//    private Long idRepairRequest;
    @ManyToOne
    @JoinColumn(name = "id_repair_request")
    private RepairRequest idRepairRequest;

    @CreationTimestamp
//    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "date_of_creation_work_order", nullable = false)
    private LocalDateTime dateOfCreationWorkOrder;

    @Column(name = "repair_status", nullable = false)
    private RepairStatus repairStatus;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

//    @Column(name = "name_of_completed_work", nullable = false)
//    private String nameOfCompletedWork;
    @ManyToOne
    @JoinColumn(name = "name_of_completed_work")
    private CompletedWork nameOfCompletedWork;

//    @Column(name = "batch_number_replaced_spare_part")
//    private String batchNumberReplacedSparePart;
    @ManyToOne
    @JoinColumn(name = "batch_number_replaced_spare_part")
    private SparePart batchNumberReplacedSparePart;

//    @Column(name = "cost_of_spare_part")
//    private BigDecimal costOfSparePart;
    @ManyToOne
    @JoinColumn(name = "cost_of_spare_part")
    private SparePart costOfSparePart;

//    @Column(name = "cost_of_work_performed", nullable = false)
//    private BigDecimal costOfWorkPerformed;
    @ManyToOne
    @JoinColumn(name = "cost_of_work_performed")
    private CompletedWork costOfWorkPerformed;

//    @Column(name = "cost_of_additional_work_performed")
//    private BigDecimal costOfAdditionalWorkPerformed;
    @ManyToOne
    @JoinColumn(name = "cost_of_additional_work_performed")
    private CompletedWork costOfAdditionalWorkPerformed;

    @Column(name = "total_cost_of_work_order", nullable = false)
    private BigDecimal totalCostOfWorkOrder;

//    @Column(name = "salary_for_employee_on_this_order", nullable = false)
//    private BigDecimal salaryForEmployeeOnThisOrder;
    @ManyToOne
    @JoinColumn(name = "salary_for_employee_on_this_order")
    private CompletedWork salaryForEmployeeOnThisOrder;
//    private Employee salaryForEmployeeOnThisOrder;

//    @Column(name = "id_of_employee_who_performed_repair", nullable = false)
//    private Long idOfEmployeeWhoPerformedRepair;
    @ManyToOne
    @JoinColumn(name = "id_of_employee_who_performed_repair")
    private Employee idOfEmployeeWhoPerformedRepair;

    @Column(name = "date_of_completion_of_repair")
    private LocalDateTime dateOfCompletionOfRepair;

}
