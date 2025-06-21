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
@Table(name = "spare_part")
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "batch_number_part", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "remaining_quantity")
    private Long remainingQuantity;

    @Column(name = "cost_spare_part", nullable = false)
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "id_device_that_uses_this_part", nullable = false)
    private Device device;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "spareParts")
    private List<WorkOrder> workOrders;
}
