package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RoleOfEmployee;
import com.yurdan.ascService.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWorkOrder_shouldCreateSuccessfully() {
        Long performedById = 3L;
        Long requestId = 1L;

        CreateWorkOrderDto dto = CreateWorkOrderDto.builder()
                .repairRequestId(requestId)
                .sparePartIds(List.of(1L))
                .completedWorkIds(List.of(2L))
                .build();

        Employee engineer = Employee.builder()
                .id(performedById)
                .role(RoleOfEmployee.ENGINEER)
                .shareOfWork(0.4F)
                .build();

        RepairRequest request = RepairRequest.builder().id(requestId).build();

        SparePart part = SparePart.builder().id(3L).cost(BigDecimal.valueOf(100)).build();
        CompletedWork work = CompletedWork.builder().id(2L).cost(BigDecimal.valueOf(200)).build();

        when(employeeRepository.findById(performedById)).thenReturn(Optional.of(engineer));
        when(repairRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(sparePartRepository.findAllById(List.of(3L))).thenReturn(List.of(part));
        when(completedWorkRepository.findAllById(List.of(2L))).thenReturn(List.of(work));

        WorkOrder savedWorkOrder = WorkOrder.builder().id(999L).build();
        when(workOrderRepository.save(any())).thenReturn(savedWorkOrder);

        WorkOrder result = workOrderService.createWorkOrder(dto, performedById);

        assertNotNull(result);
        assertEquals(999L, result.getId());
        verify(workOrderRepository).save(any(WorkOrder.class));
    }

    @Test
    void createWorkOrder_shouldThrowException_whenNotEngineer() {
        Employee notEngineer = Employee.builder()
                .id(1L)
                .role(RoleOfEmployee.RECEIVER)
                .build();

        CreateWorkOrderDto dto = CreateWorkOrderDto.builder()
                .repairRequestId(2L)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(notEngineer));

        assertThrows(EngineerRoleRequiredException.class, () ->
                workOrderService.createWorkOrder(dto, 1L)
        );
    }

    @Test
    void getFilteredWorkOrders_shouldReturnPage() {
        WorkOrder workOrder = WorkOrder.builder().id(1L).build();
        Page<WorkOrder> workOrderPage = new PageImpl<>(List.of(workOrder));
        WorkOrderResponseDto dto = WorkOrderResponseDto.builder().id(1L).build();

        when(workOrderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(workOrderPage);
        when(workOrderMapper.toDto(any())).thenReturn(dto);

        Page<WorkOrderResponseDto> result = workOrderService.getFilteredWorkOrders(
                LocalDate.now(), null, null, null, null, Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void updateWorkOrder_shouldUpdateWorkAndSpareParts_whenEngineerPerformer() {
        Long workOrderId = 1L;
        Long performedById = 4L;

        Employee performedBy = Employee.builder()
                .id(performedById)
                .role(RoleOfEmployee.ENGINEER)
                .shareOfWork(0.3F)
                .build();

        WorkOrder workOrder = WorkOrder.builder()
                .id(workOrderId)
                .performedBy(performedBy)
                .repairStatus(RepairStatus.AT_WORK)
                .build();

        UpdateWorkOrderDto dto = UpdateWorkOrderDto.builder()
                .repairStatus(RepairStatus.AT_WORK)
                .sparePartIds(List.of(1L))
                .completedWorkIds(List.of(2L))
                .build();

        SparePart part = SparePart.builder().id(1L).cost(BigDecimal.valueOf(50)).build();
        CompletedWork work = CompletedWork.builder().id(2L).cost(BigDecimal.valueOf(150)).build();

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(employeeRepository.findById(performedById)).thenReturn(Optional.of(performedBy));
        when(sparePartRepository.findAllById(dto.getSparePartIds())).thenReturn(List.of(part));
        when(completedWorkRepository.findAllById(dto.getCompletedWorkIds())).thenReturn(List.of(work));
        when(workOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        WorkOrder updated = workOrderService.updateWorkOrder(workOrderId, dto, performedById, "ENGINEER");

        assertNotNull(updated);
        assertEquals(BigDecimal.valueOf(50), updated.getCostOfSparePart());
        assertEquals(BigDecimal.valueOf(150), updated.getCostOfWork());
        assertEquals(BigDecimal.valueOf(200), updated.getTotalCost());
//        assertEquals(BigDecimal.valueOf(45), updated.getSalary());
        assertEquals(new BigDecimal("45.00"), updated.getSalary().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void updateWorkOrder_shouldThrow_whenUnauthorized() {
        Long workOrderId = 1L;
        Long employeeId = 99L;

        //  Сотрудник с ролью, НЕ ИМЕЮЩЕЙ ПРАВ
        Employee employee = Employee.builder()
                .id(employeeId)
                .role(RoleOfEmployee.STOREKEEPER) // например, STOREKEEPER
                .build();

        WorkOrder workOrder = WorkOrder.builder()
                .id(workOrderId)
                .performedBy(Employee.builder().id(3L).role(RoleOfEmployee.ENGINEER).build())
                .repairStatus(RepairStatus.AT_WORK)
                .build();

        UpdateWorkOrderDto dto = UpdateWorkOrderDto.builder().build();

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        assertThrows(UnauthorizedWorkOrderUpdateException.class, () ->
                workOrderService.updateWorkOrder(workOrderId, dto, employeeId, "STOREKEEPER")
        );
    }

    @Test
    void updateWorkOrder_shouldThrowException_whenEngineerTriesToCloseOrder() {
        // given
        Long workOrderId = 1L;
        Long employeeId = 2L;
        UpdateWorkOrderDto dto = UpdateWorkOrderDto.builder()
                .repairStatus(RepairStatus.CLOSED)
                .build();

        Employee engineer = Employee.builder()
                .id(employeeId)
                .role(RoleOfEmployee.ENGINEER)
                .shareOfWork(0.3F)
                .build();

        WorkOrder workOrder = WorkOrder.builder()
                .id(workOrderId)
                .performedBy(engineer)
                .repairStatus(RepairStatus.AT_WORK)
                .build();

        given(workOrderRepository.findById(workOrderId)).willReturn(Optional.of(workOrder));
        given(employeeRepository.findById(employeeId)).willReturn(Optional.of(engineer));

        // when & then
        assertThrows(RepairStatusViolationException.class, () ->
                workOrderService.updateWorkOrder(workOrderId, dto, employeeId, RoleOfEmployee.ENGINEER.name()));
    }
}