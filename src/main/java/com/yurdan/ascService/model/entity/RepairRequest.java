package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.util.ServiceCenterInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repair_request")
public class RepairRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    //    Чтобы дата автоматически проставлялась при изменении сущности
    @Column(name = "date_of_creation", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_repair", nullable = false)
    private TypeOfRepair typeOfRepair;

    @ManyToOne
    @JoinColumn(name = "id_of_device", nullable = false)
    private Device device;

    @Column(name = "serial_number_of_device", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "date_of_sale_of_device")
    private LocalDateTime saleDate;

    @Column(name = "reported_defect", nullable = false)
    private String defect;

    @Column(name = "appearance_of_device", nullable = false)
    private String appearance;

    @Column(name = "cost_of_repair", nullable = false)
    private BigDecimal cost;

    @Column(name = "last_name_first_name_of_consumer", nullable = false)
    private String customerFullName;

    @Column(name = "patronymic_of_consumer")
    private String customerPatronymic;

    @Column(name = "address_of_consumer", nullable = false)
    private String customerAddress;

    @Column(name = "phone_number_of_consumer", nullable = false)
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "id_of_employee_who_accepted_request", nullable = false)
    private Employee acceptedBy;

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;

    @Transient
    public String getNameOfServiceCenter() {
        return ServiceCenterInfo.NAME;
    }

    @Transient
    public String getPhoneNumberOfServiceCenter() {
        return ServiceCenterInfo.PHONE_NUMBER;
    }

    @Transient
    public String getAddressOfServiceCenter() {
        return ServiceCenterInfo.ADDRESS;
    }

//    @Column(name = "name_of_service_center", nullable = false)
//    private String nameOfServiceCenter;
//
//    @Column(name = "phone_number_of_service_center", nullable = false)
//    private String phoneNumberOfServiceCenter;
//
//    @Column(name = "address_of_service_center", nullable = false)
//    private String addressOfServiceCenter;

}
