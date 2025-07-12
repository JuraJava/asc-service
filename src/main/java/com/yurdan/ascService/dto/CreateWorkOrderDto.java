package com.yurdan.ascService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWorkOrderDto {
    private String description;
    @NotNull(message = "Repair request id is required")
    private Long repairRequestId;
    private List<Long> sparePartIds; // batchNumbers → id
    @NotNull(message = "Completed work id is required")
    private List<Long> completedWorkIds;
}

