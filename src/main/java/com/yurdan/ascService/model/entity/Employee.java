package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RoleOfEmployee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "full_name_employee", nullable = false)
    private String fullName;

    @Column(name = "patronymic")
    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleOfEmployee role;

    @Column(name = "share_of_completed_work")
    private Float shareOfWork;

//    @OneToMany(mappedBy = "acceptedById")
    @OneToMany(mappedBy = "acceptedBy")
    private List<RepairRequest> acceptedRequests;

    @OneToMany(mappedBy = "performedBy")
    private List<WorkOrder> performedRepairs;

//    @OneToMany(mappedBy = "acceptedById")
    @OneToMany(mappedBy = "acceptedBy")
    private List<PaymentTransactions> acceptedPayments;
}
