package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import lombok.Builder;

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
                                String customerFullName,
                                String customerPatronymic,
                                String customerAddress,
                                String customerPhone,
                                String acceptedBy,
                                String nameOfServiceCenter,
                                String phoneNumber,
                                String address) {


}

