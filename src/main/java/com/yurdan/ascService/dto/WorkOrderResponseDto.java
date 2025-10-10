package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс DTO используется для передачи информации о заказ-наряде.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkOrderResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private RepairStatus repairStatus;
    private PaymentStatus paymentStatus;
    private List<SparePartDto> spareParts;
    private List<CompletedWorkDto> completedWorks;
    private BigDecimal costOfSparePart;
    private BigDecimal costOfWork;
    private BigDecimal totalCost;
    private BigDecimal salary;
    private Long performedById;
    private EmployeeDto performedBy;
    private LocalDateTime completedAt;
    private Long repairRequestId;
    private String description;

}
