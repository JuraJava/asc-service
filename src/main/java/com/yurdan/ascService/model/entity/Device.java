package com.yurdan.ascService.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yurdan.ascService.model.enums.DeviceColor;
import jakarta.persistence.CascadeType;
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

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_color", nullable = false)
    private DeviceColor deviceColor;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
//    @JsonManagedReference
    private List<SparePart> spareParts;

    @OneToMany(mappedBy = "device")
    private List<RepairRequest> repairRequests;
}

