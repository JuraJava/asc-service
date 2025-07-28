package com.yurdan.ascService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Этот класс DTO, предназначенный для передачи данных при создании наряда на выполнение работ (work order) через REST API,
 * используется в запросах, поступающих с фронта или из другой системы, и содержит поля, необходимые для создания WorkOrder.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWorkOrderDto {
    @NotNull(message = "Repair request id is required")
    private Long repairRequestId;
    private List<Long> sparePartIds; // batchNumbers → id
    @NotNull(message = "Completed work id is required")
    private List<Long> completedWorkIds;
    private String description;

}

