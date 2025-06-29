package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RequestStatus;
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

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;

    @Column(name = "service_center_name", nullable = false)
    private String nameOfServiceCenter;

    @Column(name = "service_center_phone_number", nullable = false)
    private String phoneNumberOfServiceCenter;

    @Column(name = "service_center_address", nullable = false)
    private String addressOfServiceCenter;

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
                         List<WorkOrder> workOrders,
                         String nameOfServiceCenter,
                         String phoneNumberOfServiceCenter,
                         String addressOfServiceCenter) {
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
        this.workOrders = workOrders;
        this.nameOfServiceCenter = nameOfServiceCenter;
        this.phoneNumberOfServiceCenter = phoneNumberOfServiceCenter;
        this.addressOfServiceCenter = addressOfServiceCenter;
    }

    public String getNameOfCenter() {return nameOfServiceCenter; }

    public String getPhoneNumber() {
        return phoneNumberOfServiceCenter;
    }

    public String getAddress() {
        return addressOfServiceCenter;
    }

    public void setNameOfCenter(String nameOfCenter) {this.nameOfServiceCenter = nameOfCenter; }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumberOfServiceCenter = phoneNumber;
    }

    public void setAddress(String address) {
        this.addressOfServiceCenter = address;
    }
}
