package com.yurdan.ascService.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "completed_work")
public class CompletedWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name_work", nullable = false)
    private String name;

    @Column(name = "cost_work", nullable = false)
    private BigDecimal cost;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "completedWorks")
    private List<WorkOrder> workOrders;

}

// Diagnostic
// ProgramR_Repair
// Unblocking
// Replacement_Under_Warranty
// Replacement_On_Paid_Basis
// Additional_Work