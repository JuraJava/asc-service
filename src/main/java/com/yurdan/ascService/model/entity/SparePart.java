package com.yurdan.ascService.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "spare_part")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "remaining_quantity")
    private Long remainingQuantity;

    @Column(name = "reserve_quantity")
    private Long reserveQuantity;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "device_id")
//    @JsonBackReference
    private Device device;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "spareParts")
    private List<WorkOrder> workOrders;

    @Builder(toBuilder = true)
    public SparePart(Long id, String batchNumber, String description, Long remainingQuantity, Long reserveQuantity, BigDecimal cost,
                     Device device, List<WorkOrder> workOrders) {
        this.id = id;
        this.batchNumber = batchNumber;
        this.description = description;
        this.remainingQuantity = remainingQuantity;
        this.reserveQuantity = reserveQuantity;
        this.cost = cost;
        this.device = device;
        this.workOrders = workOrders;
    }
}
