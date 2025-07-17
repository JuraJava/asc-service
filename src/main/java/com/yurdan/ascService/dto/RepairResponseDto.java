package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.TypeOfRepair;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Этот класс представляет собой DTO, используемый для отправки данных о заявке на ремонт клиенту,
 * оформлен в виде record, что делает его иммутабельным (значения нельзя изменить после создания),
 * позволяет создавать экземпляры класса с помощью builder-паттерна.
 */
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

