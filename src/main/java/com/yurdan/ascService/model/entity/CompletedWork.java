package com.yurdan.ascService.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
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

    @Builder(toBuilder = true)
    public CompletedWork(Long id, String name, BigDecimal cost, List<WorkOrder> workOrders) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.workOrders = workOrders;
    }
}

// Diagnostic
// ProgramR_Repair
// Unblocking
// Replacement_Under_Warranty
// Replacement_On_Paid_Basis
// Additional_Work