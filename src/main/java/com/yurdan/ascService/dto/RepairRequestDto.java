package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepairRequestDto {
    @NotNull(message = "Device id is required")
    private Long deviceId;
    @NotNull(message = "Type of repair is required")
    private TypeOfRepair typeOfRepair;
    @NotNull(message = "Serial number is required")
    private String serialNumber;
    private LocalDate saleDate;
    @NotNull(message = "Claimed defect is required")
    private String defect;
    @NotNull(message = "Appearance is required")
    private String appearance;
    private BigDecimal cost;
    @NotNull(message = "Customer full name is required")
    private String customerFullName;
    private String customerPatronymic;
    @NotNull(message = "Customer address is required")
    private String customerAddress;
    @NotNull(message = "Customer phone is required")
    private String customerPhone;
    @NotNull(message = "Accepted by id is required")
    private Long acceptedById;

}

