package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderResponseDto {
    private String description;
    private Long id;
    private LocalDateTime createdAt;
    private RepairStatus repairStatus;
    private PaymentStatus paymentStatus;
    private List<Long> sparePartIds;
    private List<Long> completedWorkIds;
    private BigDecimal costOfSparePart;
    private BigDecimal costOfWork;
    private BigDecimal totalCost;
    private BigDecimal salary;
    private Long performedById;
    private LocalDateTime completedAt;
    private Long repairRequestId;
}
