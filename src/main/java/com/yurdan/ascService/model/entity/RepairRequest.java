package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
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
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_repair", nullable = false)
    private TypeOfRepair typeOfRepair;

    @ManyToOne
    @JoinColumn(name = "id_device", nullable = false)
    private Device device;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "date_sale")
    private LocalDate saleDate;

    @Column(name = "reported_defect", nullable = false)
    private String defect;

    @Column(name = "device_appearance", nullable = false)
    private String appearance;

    @Column(name = "repair_cost")
    private BigDecimal cost;

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

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;

    //    @Value("${service_center.name}")
    @Column(name = "service_center_name", nullable = false)
    private String nameOfServiceCenter;

    //    @Value("${service_center.phone_number}")
    @Column(name = "service_center_phone_number", nullable = false)
    private String phoneNumberOfServiceCenter;

    //    @Value("${service_center.address}")
    @Column(name = "service_center_address", nullable = false)
    private String addressOfServiceCenter;

    public String getName() {
        return nameOfServiceCenter;
    }

    public String getPhoneNumber() {
        return phoneNumberOfServiceCenter;
    }

    public String getAddress() {
        return addressOfServiceCenter;
    }

    public void setName(String name) {
        this.nameOfServiceCenter = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumberOfServiceCenter = phoneNumber;
    }

    public void setAddress(String address) {
        this.addressOfServiceCenter = address;
    }
}
