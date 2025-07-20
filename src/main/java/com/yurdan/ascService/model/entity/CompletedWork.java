package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.NameOfWork;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "name_work", nullable = false)
    private NameOfWork nameWork;

    @Column(name = "cost_work", nullable = false)
    private BigDecimal cost;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "completedWorks")
    private List<WorkOrder> workOrders;

    @Builder(toBuilder = true)
    public CompletedWork(Long id, NameOfWork nameWork, BigDecimal cost, List<WorkOrder> workOrders) {
        this.id = id;
        this.nameWork = nameWork;
        this.cost = cost;
        this.workOrders = workOrders;
    }
}
