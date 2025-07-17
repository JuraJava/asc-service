package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import lombok.*;

import java.util.List;

/**
 * Класс DTO используется для обновления наряда на выполнение работ (Work Order),
 * используется в @PutMapping или @PatchMapping в контроллере.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkOrderDto {
    private String description;
    private RepairStatus repairStatus;
    private PaymentStatus paymentStatus;
    private List<Long> sparePartIds;
    private List<Long> completedWorkIds;
    private Long repairRequestId;
    private Long newPerformedById;
}