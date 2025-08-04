package com.yurdan.ascService.controller.external;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.logging.LogHttpBody;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Это REST-контроллер, который управляет заказ-нарядами на работу в сервисном центре для внутренних запросов.
 */
@RestController
@RequestMapping("/asc")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;


    public WorkOrderController(WorkOrderService workOrderService, WorkOrderMapper workOrderMapper) {
        this.workOrderService = workOrderService;
        this.workOrderMapper = workOrderMapper;
    }

    /**
     * Создания нарядов
     */
    @PreAuthorize("hasAnyAuthority('ENGINEER')")
    @LogHttpBody
    @PostMapping("/create-work-order")
    public ResponseEntity<WorkOrderResponseDto> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderDto createWorkOrderDto,
            @RequestParam Long performedById
    ) {
        WorkOrder workOrder = workOrderService.createWorkOrder(createWorkOrderDto, performedById);
        WorkOrderResponseDto responseDto = workOrderMapper.toDto(workOrder);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Изменение нарядов.
     */
    @PreAuthorize("hasAnyAuthority('ENGINEER','RECEIVER','ADMINISTRATOR')")
    @PutMapping("/update-work-order/{workOrderId}")
    public ResponseEntity<WorkOrderResponseDto> updateWorkOrder(
            @PathVariable Long workOrderId,
            @Valid @RequestBody UpdateWorkOrderDto dto,
            @RequestParam Long employeeId
    ) {
          WorkOrder updatedWorkOrder = workOrderService.updateWorkOrder(workOrderId, dto, employeeId);
        WorkOrderResponseDto responseDto = workOrderMapper.toDto(updatedWorkOrder);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Получение списка нарядов с фильтрацией.
     */
    @PreAuthorize("hasAnyAuthority('ENGINEER', 'RECEIVER', 'ADMINISTRATOR')")
    @LogHttpBody
    @GetMapping("/work-orders")
    public ResponseEntity<Page<WorkOrderResponseDto>> getWorkOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate,
            @RequestParam(required = false) RepairStatus repairStatus,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) Long repairRequestId,
            @RequestParam(required = false) Long performedById,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<WorkOrderResponseDto> responseDto = workOrderService.getFilteredWorkOrders(
                completedDate, repairStatus, paymentStatus, repairRequestId, performedById, pageRequest
        );
        return ResponseEntity.ok(responseDto);
    }
}