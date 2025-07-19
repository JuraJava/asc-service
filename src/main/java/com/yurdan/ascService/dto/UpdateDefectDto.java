package com.yurdan.ascService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс DTO, предназначенный для обновления информации о неисправности (defect) в заявке на ремонт
 */
@Getter
@Setter
public class UpdateDefectDto {
    @NotNull
    private Long id;

    @NotBlank
    private String defect;
}