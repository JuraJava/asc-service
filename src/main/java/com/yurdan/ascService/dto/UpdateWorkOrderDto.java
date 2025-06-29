package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class UpdateWorkOrderDto {
    private String description;
    private RepairStatus repairStatus;
    private PaymentStatus paymentStatus;
    private List<Long> sparePartIds;
    private List<Long> completedWorkIds;
    private Long repairRequestId;
    private Long newPerformedById;
}