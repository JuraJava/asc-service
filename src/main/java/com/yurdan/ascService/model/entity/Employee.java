package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RoleOfEmployee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
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

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleOfEmployee role;

    @Column(name = "share_of_completed_work")
    private Float shareOfWork;

    @OneToMany(mappedBy = "acceptedBy")
    private List<RepairRequest> acceptedRequests;

    @OneToMany(mappedBy = "performedBy")
    private List<WorkOrder> performedRepairs;

    @OneToMany(mappedBy = "acceptedBy")
    private List<PaymentTransactions> acceptedPayments;
}

