package com.yurdan.ascService.controller.external;

import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Это REST-контроллер, который управляет заказ-нарядами для внешних запросов.
 */
@RestController
@RequestMapping("/asc/external/")
public class ExternalWorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;

    public ExternalWorkOrderController(WorkOrderService workOrderService, WorkOrderMapper workOrderMapper) {
        this.workOrderService = workOrderService;
        this.workOrderMapper = workOrderMapper;
    }
    /**
     * Получения заказ-наряда по ID заявки на ремонт.
     */
    @GetMapping("/work-orders/by-repair-request/{repairRequestId}")
    public ResponseEntity<WorkOrderResponseDto> getByRepairRequestId(@PathVariable Long repairRequestId) {
        Optional<WorkOrder> optional = workOrderService.getByRepairRequestId(repairRequestId);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WorkOrderResponseDto dto = workOrderMapper.toDto(optional.get());
        return ResponseEntity.ok(dto);
    }

    /**
     * Получения заказ-наряда по его ID.
     */
    @GetMapping("/work-orders/{id}")
    public ResponseEntity<WorkOrderResponseDto> getWorkOrderById(@PathVariable Long id) {
        Optional<WorkOrder> optional = workOrderService.getById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WorkOrderResponseDto dto = workOrderMapper.toDto(optional.get());
        return ResponseEntity.ok(dto);
    }
}

