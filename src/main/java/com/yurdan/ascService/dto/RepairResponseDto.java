package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record RepairResponseDto(Long id,
                                String device,
                                TypeOfRepair typeOfRepair,
                                LocalDateTime createdAt,
                                String serialNumber,
                                LocalDate saleDate,
                                String defect,
                                String appearance,
                                BigDecimal cost,
                                String customerFullName,
                                String customerPatronymic,
                                String customerAddress,
                                String customerPhone,
                                String acceptedBy,
                                String name,
                                String phoneNumber,
                                String address) {

    public RepairResponseDto(){
        this(null, null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null);
    }

    public RepairResponseDto(Long id){
        this(id, null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null);
    }
}

