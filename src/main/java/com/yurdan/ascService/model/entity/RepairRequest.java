package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.TypeOfRepair;
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
    private LocalDateTime dateOfCreation;

    @Column(name = "type_of_repair", nullable = false)
    private TypeOfRepair typeOfRepair;

//    @Column(name = "id_of_device", nullable = false)
//    private Long idOfDevice;
    @ManyToOne
    @JoinColumn(name = "id_of_device")
    private Device idOfDevice;

    @Column(name = "serial_number_of_device", nullable = false, unique = true)
    private String serialNumberOfDevice;

    @Column(name = "date_of_sale_of_device")
    private LocalDateTime dateOfSaleOfDevice;

    @Column(name = "reported_defect", nullable = false)
    private String reportedDefect;

    @Column(name = "appearance_of_device", nullable = false)
    private String appearanceOfDevice;

    @Column(name = "cost_of_repair", nullable = false)
    private BigDecimal costOfRepair;

    @Column(name = "last_name_first_name_of_consumer", nullable = false)
    private String lastNameFirstNameOfConsumer;

    @Column(name = "patronymic_of_consumer")
    private String patronymicOfConsumer;

    @Column(name = "address_of_consumer", nullable = false)
    private String addressOfConsumer;

    @Column(name = "phone_number_of_consumer", nullable = false)
    private String phoneNumberOfConsumer;

//    @Column(name = "name_of_service_center", nullable = false)
//    private String nameOfServiceCenter;
//
//    @Column(name = "phone_number_of_service_center", nullable = false)
//    private String phoneNumberOfServiceCenter;
//
//    @Column(name = "address_of_service_center", nullable = false)
//    private String addressOfServiceCenter;

//    @Column(name = "id_employee_who_accepted_request", nullable = false)
//    private Long idEmployeeWhoAcceptedRequest;
    @ManyToOne
    @JoinColumn (name = "id_of_employee_who_accepted_request")
    private Employee idOfEmployeeWhoAcceptedRequest;

    @OneToMany(mappedBy = "repair_request", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;

}
