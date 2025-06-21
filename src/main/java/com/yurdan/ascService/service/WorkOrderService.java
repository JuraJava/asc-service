package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RoleOfEmployee;
import com.yurdan.ascService.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class WorkOrderService {

    private final RepairRequestRepository repairRequestRepository;
    private final WorkOrderRepository workOrderRepository;
    private final EmployeeRepository employeeRepository;
    private final SparePartRepository sparePartRepository;
    private final CompletedWorkRepository completedWorkRepository;
    private final WorkOrderMapper workOrderMapper;

    public WorkOrderService(
            RepairRequestRepository repairRequestRepository,
            WorkOrderRepository workOrderRepository,
            EmployeeRepository employeeRepository,
            SparePartRepository sparePartRepository,
            CompletedWorkRepository completedWorkRepository,
            WorkOrderMapper workOrderMapper
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.workOrderRepository = workOrderRepository;
        this.employeeRepository = employeeRepository;
        this.sparePartRepository = sparePartRepository;
        this.completedWorkRepository = completedWorkRepository;
        this.workOrderMapper = workOrderMapper;
    }

    @Transactional
    public WorkOrder createWorkOrder(CreateWorkOrderDto createWorkOrderDto, Long performedById) {
        Employee performedBy = employeeRepository.findById(performedById)
                .orElseThrow(() -> new EngineerNotFoundException(performedById));

        if (performedBy.getRole() != RoleOfEmployee.ENGINEER) {
            throw new EngineerRoleRequiredException(performedBy.getRole());
        }

        RepairRequest request = repairRequestRepository.findById(createWorkOrderDto.getRepairRequestId())
                .orElseThrow(() -> new RepairRequestNotFoundException(createWorkOrderDto.getRepairRequestId()));

        List<SparePart> parts = sparePartRepository.findAllById(createWorkOrderDto.getSparePartIds());




        BigDecimal totalSparePartCost = parts.stream()
                .map(SparePart::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CompletedWork> works = completedWorkRepository.findAllById(createWorkOrderDto.getCompletedWorkIds());
        BigDecimal totalWorkCost = works.stream()
                .map(CompletedWork::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = totalWorkCost.add(totalSparePartCost);
        BigDecimal salary = totalWorkCost.multiply(BigDecimal.valueOf(performedBy.getShareOfWork()));

        WorkOrder workOrder = new WorkOrder();
        workOrder.setRepairRequest(request);
        workOrder.setPerformedBy(performedBy);
        workOrder.setRepairStatus(RepairStatus.AT_WORK);
        workOrder.setPaymentStatus(PaymentStatus.UNPAID);
        workOrder.setCostOfSparePart(totalSparePartCost);
        workOrder.setCostOfWork(totalWorkCost);
        workOrder.setTotalCost(totalCost);
        workOrder.setSalary(salary);
        workOrder.setCompletedWorks(new ArrayList<>(works));
        workOrder.setSpareParts(new ArrayList<>(parts));

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public Page<WorkOrderResponseDto> getFilteredWorkOrders(
            LocalDate completedDate,
            RepairStatus repairStatus,
            PaymentStatus paymentStatus,
            Long repairRequestId,
            Long performedById,
            Pageable pageable) {

        Specification<WorkOrder> spec = Specification.where(null);

        if (completedDate != null) {
            LocalDateTime startOfDay = completedDate.atStartOfDay();
            LocalDateTime endOfDay = completedDate.atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) -> cb.between(root.get("completedAt"), startOfDay, endOfDay));
        }

        if (repairStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("repairStatus"), repairStatus));
        }

        if (paymentStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentStatus"), paymentStatus));
        }

        if (repairRequestId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("repairRequest").get("id"), repairRequestId));
        }

        if (performedById != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("performedBy").get("id"), performedById));
        }

        return workOrderRepository.findAll(spec, pageable)
                .map(workOrderMapper::toDto);
    }

    @Transactional
    public WorkOrder updateWorkOrder(Long workOrderId, UpdateWorkOrderDto dto, Long employeeId, String role) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFoundException(workOrderId));

        Employee editor = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        boolean isEngineerPerformer = workOrder.getPerformedBy().getId().equals(editor.getId())
                && editor.getRole() == RoleOfEmployee.ENGINEER;

        boolean isPrivileged = editor.getRole() == RoleOfEmployee.RECEIVER
                || editor.getRole() == RoleOfEmployee.ADMINISTRATOR;

        boolean modified = false;

        if (isEngineerPerformer) {
            if (dto.getRepairStatus() != null) {
                if (dto.getRepairStatus() == RepairStatus.CLOSED) {
                    throw new RepairStatusViolationException(dto.getRepairStatus());
                }
                workOrder.setRepairStatus(dto.getRepairStatus());
                modified = true;
                if (workOrder.getRepairStatus() != RepairStatus.CLOSED
                        && dto.getRepairStatus() == RepairStatus.CANCELLED) {
                        workOrder.setRepairStatus(RepairStatus.CANCELLED);
                        workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                        workOrder.setCostOfSparePart(null);
                        workOrder.setCostOfWork(null);
                        workOrder.setSpareParts(null);
                        workOrder.setCompletedWorks(null);
                        workOrder.setCompletedAt(null);
                }
                if (dto.getRepairStatus() == RepairStatus.COMPLETED) {
                    workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                    workOrder.setCompletedAt(LocalDateTime.now());
                } else if (dto.getRepairStatus() == RepairStatus.AT_WORK) {
                    workOrder.setCompletedAt(null);
                    workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                }
            }
            if (dto.getSparePartIds() != null) {
                List<SparePart> parts = sparePartRepository.findAllById(dto.getSparePartIds());
                workOrder.setSpareParts(new ArrayList<>(parts));
                modified = true;
            }
            if (dto.getCompletedWorkIds() != null) {
                List<CompletedWork> works = completedWorkRepository.findAllById(dto.getCompletedWorkIds());
                workOrder.setCompletedWorks(new ArrayList<>(works));
                modified = true;
            }
            if (modified) {
                BigDecimal totalSparePartCost = Optional.ofNullable(workOrder.getSpareParts())
                        .orElse(List.of())
                        .stream()
                        .map(SparePart::getCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalWorkCost = Optional.ofNullable(workOrder.getCompletedWorks())
                        .orElse(List.of())
                        .stream()
                        .map(CompletedWork::getCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCost = totalWorkCost.add(totalSparePartCost);
                BigDecimal salary = totalWorkCost.multiply(BigDecimal.valueOf(workOrder.getPerformedBy().getShareOfWork()));

                workOrder.setCostOfSparePart(totalSparePartCost);
                workOrder.setCostOfWork(totalWorkCost);
                workOrder.setTotalCost(totalCost);
                workOrder.setSalary(salary);
            }
        }
        if (isPrivileged) {
            if (workOrder.getRepairStatus() == RepairStatus.COMPLETED &&
                    (dto.getPaymentStatus() == PaymentStatus.PAID || dto.getRepairStatus() == RepairStatus.CLOSED)) {
                workOrder.setPaymentStatus(PaymentStatus.PAID);
                workOrder.setRepairStatus(RepairStatus.CLOSED);

            }
        }
        if (!isEngineerPerformer && !isPrivileged) {
            throw new UnauthorizedWorkOrderUpdateException("You are not allowed to modify this work order.");
        }
        return workOrderRepository.save(workOrder);
    }
}