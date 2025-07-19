package com.yurdan.ascService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс DTO используется для передачи данных между слоями приложения:
 * из БД → в сервис → в контроллер → в API
 * или в обратную сторону — из запроса клиента → в контроллер → в сервис,
 * в данном случае SparePartDto — это представление запасной части.
 */
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
    //  БЕЗ поля Device чтобы избежать циклических зависимостей
}