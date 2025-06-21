package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepairResponseDto {

    private Long id;
    private Long deviceId;
    private TypeOfRepair typeOfRepair;
    private LocalDateTime createdAt;
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

