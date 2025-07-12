package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import lombok.*;

import java.util.List;


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