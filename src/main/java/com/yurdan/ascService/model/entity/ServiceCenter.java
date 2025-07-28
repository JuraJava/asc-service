package com.yurdan.ascService.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_center")
public class ServiceCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "service_center_name", nullable = false)
    private String serviceCenterName;

    @Column(name = "service_center_phone_number", nullable = false)
    private String serviceCenterPhoneNumber;

    @Column(name = "service_center_address", nullable = false)
    private String serviceCenterAddress;

    @OneToMany(mappedBy = "serviceCenter")
    private List<RepairRequest> repairRequest;
}

