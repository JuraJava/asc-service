package com.yurdan.ascService.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String batch_number_spare_part;

    @Column(name = "number_of_remaining_spare_part")
    private Long numberOfRemainingSparePart;

    @Column(name = "cost_of_spare_part", nullable = false)
    private BigDecimal costOfSparePart;

    @Column(name = "id_of_device_for_which_spare_part_is_use", nullable = false)
    private Long idOfDeviceForWhichSparePartIsUse;

    @OneToMany(mappedBy = "spare_part", cascade = CascadeType.ALL)
    private List<SparePart> costs_of_spare_part;

    @OneToMany(mappedBy = "spare_part", cascade = CascadeType.ALL)
    private List<SparePart> batch_numbers_spare_part;

}
