package com.yurdan.ascService.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    @Column(name = "name_of_work", nullable = false, unique = true)
    private String nameOfWork;

    @Column(name = "cost_of_work", nullable = false)
    private BigDecimal costOfWork;

    @OneToMany(mappedBy = "completed_work", cascade = CascadeType.ALL)
    private List<CompletedWork> namesOfCompletedWork;

    @OneToMany(mappedBy = "completed_work", cascade = CascadeType.ALL)
    private List<CompletedWork> works_performed;

    @OneToMany(mappedBy = "completed_work", cascade = CascadeType.ALL)
    private List<CompletedWork> additional_works_performed;

    @OneToMany(mappedBy = "completed_work", cascade = CascadeType.ALL)
    private List<CompletedWork> salariesForEmployeeOnThisOrder;

}

//DIAGNOSTICS
//PROGRAM_REPAIR
//UNBLOCKING
//REPLACEMENT_UNDER_WARRANTY
//REPLACEMENT_ON_PAID_BASIS
//additional work