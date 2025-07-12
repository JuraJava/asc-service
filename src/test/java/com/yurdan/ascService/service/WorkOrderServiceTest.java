package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.*;
import com.yurdan.ascService.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SparePartRepository sparePartRepository;

    @Mock
    private CompletedWorkRepository completedWorkRepository;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @InjectMocks
    private WorkOrderService workOrderService;

    private Employee engineer;
    private RepairRequest request;
    private CompletedWork completedWork;
    private SparePart sparePart;

    @BeforeEach
    void setUp() {
        engineer = Employee.builder()
                .id(1L)
                .role(RoleOfEmployee.ENGINEER)
                .shareOfWork(0.5f)
                .build();

        request = RepairRequest.builder()
                .id(100L)
                .requestStatus(RequestStatus.ACCEPTED)
                .build();

        completedWork = CompletedWork.builder()
                .id(10L)
                .cost(new BigDecimal("100.00"))
                .build();

        sparePart = SparePart.builder()
                .id(20L)
                .batchNumber("GH97-12312A")
                .remainingQuantity(5L)
                .reserveQuantity(0L)
                .cost(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void createWorkOrder_successful() {
        CreateWorkOrderDto dto = CreateWorkOrderDto.builder()
                .repairRequestId(request.getId())
                .description("Fixing issue")
                .completedWorkIds(List.of(completedWork.getId()))
                .sparePartIds(List.of(sparePart.getId()))
                .build();

        when(employeeRepository.findById(engineer.getId())).thenReturn(Optional.of(engineer));
        when(repairRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(completedWorkRepository.findAllById(dto.getCompletedWorkIds())).thenReturn(List.of(completedWork));
        when(sparePartRepository.findAllById(dto.getSparePartIds())).thenReturn(List.of(sparePart));
        when(workOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkOrder result = workOrderService.createWorkOrder(dto, engineer.getId());

        assertNotNull(result);
        assertEquals(PaymentStatus.UNPAID, result.getPaymentStatus());
        assertEquals(RepairStatus.AT_WORK, result.getRepairStatus());
        assertEquals(request, result.getRepairRequest());
        assertEquals(engineer, result.getPerformedBy());
//        assertEquals(BigDecimal.valueOf(50), result.getCostOfSparePart());
        assertTrue(BigDecimal.valueOf(50).compareTo(result.getCostOfSparePart()) == 0);
        assertTrue(BigDecimal.valueOf(100).compareTo(result.getCostOfWork()) == 0);
        assertTrue(BigDecimal.valueOf(150).compareTo(result.getTotalCost()) == 0);
        assertTrue(BigDecimal.valueOf(50).compareTo(result.getSalary()) == 0);

        verify(sparePartRepository).save(sparePart);
        assertEquals(4, sparePart.getRemainingQuantity());
        assertEquals(1, sparePart.getReserveQuantity());
    }

    @Test
    void createWorkOrder_shouldThrowException_whenEngineerNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        CreateWorkOrderDto dto = CreateWorkOrderDto.builder()
                .repairRequestId(100L)
                .build();

        assertThrows(EmployeeNotFoundException.class, () -> {
            workOrderService.createWorkOrder(dto, 1L);
        });
    }

    @Test
    void createWorkOrder_shouldThrowException_whenNotEngineer() {
        engineer.setRole(RoleOfEmployee.RECEIVER);
        when(employeeRepository.findById(engineer.getId())).thenReturn(Optional.of(engineer));

        CreateWorkOrderDto dto = CreateWorkOrderDto.builder()
                .repairRequestId(100L)
                .build();

        assertThrows(EngineerRoleRequiredException.class, () -> {
            workOrderService.createWorkOrder(dto, engineer.getId());
        });
    }

    @Test
    void updateWorkOrder_shouldThrow_whenOrderNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(engineer));
        when(workOrderRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateWorkOrderDto dto = new UpdateWorkOrderDto();

        assertThrows(WorkOrderNotFoundException.class, () ->
                workOrderService.updateWorkOrder(99L, dto, 1L));
    }

    @Test
    void updateWorkOrder_shouldThrow_whenUnauthorized() {
        WorkOrder order = WorkOrder.builder()
                .id(10L)
                .repairStatus(RepairStatus.AT_WORK)
                .performedBy(Employee.builder().id(2L).role(RoleOfEmployee.ENGINEER).build())
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(engineer));
        when(workOrderRepository.findById(10L)).thenReturn(Optional.of(order));

        UpdateWorkOrderDto dto = new UpdateWorkOrderDto();

        assertThrows(UnauthorizedWorkOrderUpdateException.class, () ->
                workOrderService.updateWorkOrder(10L, dto, 1L));
    }
}

