package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RoleOfEmployee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String lastNameFirstNameOfEmployee;

    @Column(name = "patronymic_of_employee")
    private String patronymicOfEmployee;

    @Column(name = "role_of_employee", nullable = false)
    private RoleOfEmployee roleOfEmployee;

}
