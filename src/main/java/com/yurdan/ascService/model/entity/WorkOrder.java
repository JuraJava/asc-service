package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
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

    @CreationTimestamp
    //    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "date_creation_work_order", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "repair_status", nullable = false)
    private RepairStatus repairStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "cost_replaced_part")
    private BigDecimal costOfSparePart;

    @Column(name = "cost_performed_work", nullable = false)
    private BigDecimal costOfWork;

    @Column(name = "total_cost_work_order", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "salary_for_this_order", nullable = false)
    private BigDecimal salary;

    @Column(name = "date_completion_repair")
    private LocalDateTime completedAt;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_repair_request", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne
    @JoinColumn(name = "id_employee_who_performed", nullable = false)
    private Employee performedBy;

    @ManyToMany
    @JoinTable(
            name = "completed_work_work_order",
            joinColumns = @JoinColumn(name = "work_order_id"),
            inverseJoinColumns = @JoinColumn(name = "completed_work_id"))
    private List<CompletedWork> completedWorks;

    @ManyToMany
    @JoinTable(
            name = "spare_part_work_order",
            joinColumns = @JoinColumn(name = "work_order_id"),
            inverseJoinColumns = @JoinColumn(name = "spare_part_id"))
    private List<SparePart> spareParts;

}
