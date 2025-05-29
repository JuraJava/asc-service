package com.yurdan.ascService.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
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

    @Column(name = "batch_number_spare_part", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "number_of_remaining_spare_part")
    private Long remainingQuantity;

    @Column(name = "cost_of_spare_part", nullable = false)
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "id_of_device_for_which_spare_part_is_use", nullable = false)
    private Device device;
}
