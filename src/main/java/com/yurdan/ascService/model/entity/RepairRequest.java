package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RequestStatus;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "repair_request")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_repair", nullable = false)
    private TypeOfRepair typeOfRepair;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus requestStatus;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "id_service_center", nullable = false)
    private ServiceCenter serviceCenter;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "date_sale")
    private LocalDate saleDate;

    @Column(name = "reported_defect", nullable = false)
    private String defect;

    @Column(name = "device_appearance", nullable = false)
    private String appearance;

    @Column(name = "full_name_consumer", nullable = false)
    private String customerFullName;

    @Column(name = "patronymic_consumer")
    private String customerPatronymic;

    @Column(name = "address_consumer", nullable = false)
    private String customerAddress;

    @Column(name = "phone_number_consumer", nullable = false)
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "id_employee_who_accepted", nullable = false)
    private Employee acceptedBy;

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkOrder> workOrders;

    @OneToOne(mappedBy = "repairRequest", cascade = CascadeType.ALL)
    private PaymentTransactions paymentTransactions;

    @Builder(toBuilder = true)
    public RepairRequest(Long id,
                         LocalDateTime createdAt,
                         TypeOfRepair typeOfRepair,
                         RequestStatus requestStatus,
                         Device device,
                         String serialNumber,
                         LocalDate saleDate,
                         String defect,
                         String appearance,
                         String customerFullName,
                         String customerPatronymic,
                         String customerAddress,
                         String customerPhone,
                         Employee acceptedBy,
                         ServiceCenter serviceCenter,
                         List<WorkOrder> workOrders,
                         PaymentTransactions paymentTransactions) {
        this.id = id;
        this.createdAt = createdAt;
        this.typeOfRepair = typeOfRepair;
        this.requestStatus = requestStatus;
        this.device = device;
        this.serialNumber = serialNumber;
        this.saleDate = saleDate;
        this.defect = defect;
        this.appearance = appearance;
        this.customerFullName = customerFullName;
        this.customerPatronymic = customerPatronymic;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.acceptedBy = acceptedBy;
        this.serviceCenter = serviceCenter;
        this.workOrders = workOrders;
        this.paymentTransactions = paymentTransactions;

    }
}
