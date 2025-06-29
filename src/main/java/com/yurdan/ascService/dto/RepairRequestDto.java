package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepairRequestDto {

    @NotNull(message = "Device id is required")
    private Long deviceId;

    @NotNull(message = "Type of repair is required")
    private TypeOfRepair typeOfRepair;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    private LocalDate saleDate;

    @NotBlank(message = "Claimed defect is required")
    private String defect;

    @NotBlank(message = "Appearance is required")
    private String appearance;

    @NotBlank(message = "Customer full name is required")
    private String customerFullName;

    private String customerPatronymic;

    @NotBlank(message = "Customer address is required")
    private String customerAddress;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotNull(message = "Accepted by id is required")
    private Long acceptedById;
}

