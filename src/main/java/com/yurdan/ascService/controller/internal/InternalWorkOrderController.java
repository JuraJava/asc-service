package com.yurdan.ascService.controller.internal;

import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Это REST-контроллер, который управляет заказ-нарядами для внешних запросов.
 */
@RestController
@RequestMapping("/asc/internal/")
public class InternalWorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;

    public InternalWorkOrderController(WorkOrderService workOrderService, WorkOrderMapper workOrderMapper) {
        this.workOrderService = workOrderService;
        this.workOrderMapper = workOrderMapper;
    }

    /**
     * Получение заказ-нарядов по ID заявки на ремонт (с подгрузкой работ и запчастей).
     */
    @GetMapping("/work-orders/by-repair-request/{repairRequestId}")
    public ResponseEntity<List<WorkOrderResponseDto>> getByRepairRequestId(@PathVariable Long repairRequestId) {
        //  Используем метод с JOIN FETCH
        List<WorkOrder> orders = workOrderService.getAllByRepairRequestIdWithDetails(repairRequestId);

        List<WorkOrderResponseDto> dtos = orders.stream()
                .map(workOrderMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Получение одного заказ-наряда по его ID (с подгрузкой работ и запчастей).
     */
    @GetMapping("/work-orders/{id}")
    public ResponseEntity<WorkOrderResponseDto> getWorkOrderById(@PathVariable Long id) {
        //  Используем метод с JOIN FETCH
        Optional<WorkOrder> optional = workOrderService.getByIdWithDetails(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WorkOrderResponseDto dto = workOrderMapper.toDto(optional.get());
        return ResponseEntity.ok(dto);
    }
}

