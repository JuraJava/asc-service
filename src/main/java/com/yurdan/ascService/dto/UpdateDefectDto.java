package com.yurdan.ascService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDefectDto {
    @NotNull
    private Long id;

    @NotBlank
    private String defect;
}