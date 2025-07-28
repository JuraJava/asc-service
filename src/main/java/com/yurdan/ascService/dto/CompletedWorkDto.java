package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.NameOfWork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedWorkDto {
    private Long id;
    private NameOfWork nameWork;
    private BigDecimal cost;
}
