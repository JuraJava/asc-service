package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.DeviceColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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
    private List<SparePart> spareParts;

    @OneToMany(mappedBy = "device")
    private List<RepairRequest> repairRequests;
}

