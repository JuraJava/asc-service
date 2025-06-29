package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "repair_request", schema = "asc_service")
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
    @JoinColumn(name = "device_id", nullable = false)
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
