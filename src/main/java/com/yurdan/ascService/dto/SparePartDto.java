package com.yurdan.ascService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SparePartDto {
    private Long id;
    private String batchNumber;
    private String description;
    private Long remainingQuantity;
    private Long reserveQuantity;
    private BigDecimal cost;
    //  БЕЗ поля Device!
}