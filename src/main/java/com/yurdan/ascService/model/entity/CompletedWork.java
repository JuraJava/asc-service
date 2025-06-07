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
@Table(name = "completed_work")
public class CompletedWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name_work", nullable = false, unique = true)
    private String name;

    @Column(name = "cost_work", nullable = false)
    private BigDecimal cost;
}

//DIAGNOSTICS
//PROGRAM_REPAIR
//UNBLOCKING
//REPLACEMENT_UNDER_WARRANTY
//REPLACEMENT_ON_PAID_BASIS
//additionalWork