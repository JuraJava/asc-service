package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RoleOfEmployee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "employee")
//public class Employee {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @Column(name = "last_name_first_name_of_employee", nullable = false)
//    private String lastNameFirstNameOfEmployee;
//
//    @Column(name = "patronymic_of_employee")
//    private String patronymicOfEmployee;
//
//    @Column(name = "role_of_employee", nullable = false)
//    private RoleOfEmployee roleOfEmployee;
//
//    @Column(name = "share_of_employee_of_cost_of_completed_work")
//    private int shareOfEmployeeOfCostOfCompletedWork;
//
//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
//    private List<Employee> employeesWhoAcceptedRequest;
//
//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
//    private List<Employee> employeesWhoPerformedRepair;
//
//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
//    private List<Employee> employeesWhoAcceptedPayment;
//
//}

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

    @Column(name = "last_name_first_name_of_employee", nullable = false)
    private String fullName;

    @Column(name = "patronymic_of_employee")
    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_of_employee", nullable = false)
    private RoleOfEmployee role;

    @Column(name = "share_of_employee_of_cost_of_completed_work")
    private Integer shareOfWork;

    @OneToMany(mappedBy = "acceptedBy")
    private List<RepairRequest> acceptedRequests;

    @OneToMany(mappedBy = "performedBy")
    private List<WorkOrder> performedRepairs;

    @OneToMany(mappedBy = "acceptedBy")
    private List<PaymentTransactions> acceptedPayments;
}
