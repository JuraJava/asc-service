package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepairResponseDto {

    private Long id;
    private LocalDateTime createdAt;
    private TypeOfRepair typeOfRepair;
    private Long deviceId;
    private String serialNumber;
    private LocalDate saleDate;
    private String defect;
    private String appearance;
    private BigDecimal cost;
    private String customerFullName;
    private String customerPatronymic;
    private String customerAddress;
    private String customerPhone;
    private Long acceptedById;
    private String name;
    private String phoneNumber;
    private String address;
}

