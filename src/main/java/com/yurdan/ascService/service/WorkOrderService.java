package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RequestStatus;
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
import java.util.stream.Collectors;
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
    public WorkOrder createWorkOrder(CreateWorkOrderDto dto, Long performedById) {
        Employee performer = employeeRepository.findById(performedById)
                .orElseThrow(() -> new EmployeeNotFoundException(performedById));

        if (performer.getRole() != RoleOfEmployee.ENGINEER) {
            throw new EngineerRoleRequiredException(performer.getRole());
        }

        RepairRequest repairRequest = repairRequestRepository.findById(dto.getRepairRequestId())
                .orElseThrow(() -> new RepairRequestNotFoundException(dto.getRepairRequestId()));

        if (repairRequest.getRequestStatus() == RequestStatus.ON_PAYMENT || repairRequest.getRequestStatus() == RequestStatus.CLOSED) {
            throw new ProhibitionCreateOrderBasedOnClosedOOrOnPaymentRequestException("Создать заказ " +
                    "на основании уже закрытой заявки или заявки, которая на оплате, невозможно.");
        }

        repairRequest.setRequestStatus(RequestStatus.AT_WORK);

        List<CompletedWork> completedWorks = completedWorkRepository.findAllById(dto.getCompletedWorkIds());
        List<SparePart> spareParts = sparePartRepository.findAllById(dto.getSparePartIds());

        for (SparePart part : spareParts) {

            if (part.getRemainingQuantity() < 1) {
                throw new SparePartUnavailableException(part.getBatchNumber());
            }

            part.setRemainingQuantity(part.getRemainingQuantity() - 1);
            part.setReserveQuantity((part.getReserveQuantity() != null ? part.getReserveQuantity() : 0) + 1);
            sparePartRepository.save(part);
        }

        BigDecimal totalWorkCost = completedWorks.stream().map(CompletedWork::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPartCost = spareParts.stream().map(SparePart::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = totalWorkCost.add(totalPartCost);
        BigDecimal salary = totalWorkCost.multiply(BigDecimal.valueOf(performer.getShareOfWork()));

        WorkOrder workOrder = WorkOrder.builder()
                .repairStatus(RepairStatus.AT_WORK)
                .paymentStatus(PaymentStatus.UNPAID)
                .repairRequest(repairRequest)
                .performedBy(performer)
                .completedWorks(completedWorks)
                .spareParts(spareParts)
                .costOfWork(totalWorkCost)
                .costOfSparePart(totalPartCost)
                .totalCost(totalCost)
                .salary(salary)
                .description(dto.getDescription())
                .build();

        return workOrderRepository.save(workOrder);
    }

    public WorkOrder updateWorkOrder(Long workOrderId, UpdateWorkOrderDto dto, Long employeeId) {
        Employee editor = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFoundException(workOrderId));

        if (workOrder.getRepairStatus() == RepairStatus.CLOSED) {
            throw new RepairStatusViolationException("Закрытый заказ изменить невозможно.");
        }

        boolean isEngineerPerformer = workOrder.getPerformedBy().getId().equals(editor.getId()) &&
                editor.getRole() == RoleOfEmployee.ENGINEER;

        boolean isPrivileged = editor.getRole() == RoleOfEmployee.RECEIVER ||
                editor.getRole() == RoleOfEmployee.ADMINISTRATOR;

        if (!isEngineerPerformer && !isPrivileged) {
            throw new UnauthorizedWorkOrderUpdateException("Вам не разрешено изменять этот заказ.");
        }

        return updateWorkOrderTransactional(workOrder, dto, editor);
    }

    @Transactional
    protected WorkOrder updateWorkOrderTransactional(WorkOrder workOrder, UpdateWorkOrderDto dto, Employee editor) {
        boolean modified = false;

        if (dto.getNewPerformedById() != null &&
                workOrder.getRepairStatus() != RepairStatus.CANCELLED &&
                workOrder.getRepairStatus() != RepairStatus.CLOSED) {

            Employee newPerformer = employeeRepository.findById(dto.getNewPerformedById())
                    .orElseThrow(() -> new EngineerNotFoundException(dto.getNewPerformedById()));

            if (newPerformer.getRole() != RoleOfEmployee.ENGINEER) {
                throw new EngineerRoleRequiredException(newPerformer.getRole());
            }

            workOrder.setPerformedBy(newPerformer);
            modified = true;
        }

        RepairStatus originalStatus = workOrder.getRepairStatus();

        if (dto.getRepairStatus() != null) {
            if (dto.getRepairStatus() == RepairStatus.CLOSED) {
                boolean isPrivileged = editor.getRole() == RoleOfEmployee.RECEIVER ||
                        editor.getRole() == RoleOfEmployee.ADMINISTRATOR;

                if (!isPrivileged && !editor.getId().equals(workOrder.getPerformedBy().getId())) {
                    throw new UnauthorizedWorkOrderUpdateException("Только исполнитель или привилегированный персонал может закрыть заказ");
                }

                if (originalStatus != RepairStatus.COMPLETED) {
                    throw new RepairStatusViolationException("Заказ может быть закрыт только после завершения ремонта (COMPLETED).");
                }

                workOrder.setRepairStatus(RepairStatus.CLOSED);
                workOrder.setPaymentStatus(PaymentStatus.PAID);

                if (workOrder.getSpareParts() != null) {
                    for (SparePart part : workOrder.getSpareParts()) {
                        if (part.getReserveQuantity() > 0) {
                            part.setReserveQuantity(part.getReserveQuantity() - 1);
                            sparePartRepository.save(part);
                        }
                    }
                }

                modified = true;
            } else {
                workOrder.setRepairStatus(dto.getRepairStatus());
                modified = true;

                if (dto.getRepairStatus() == RepairStatus.CANCELLED) {
                    if (workOrder.getSpareParts() != null) {
                        for (SparePart part : workOrder.getSpareParts()) {
                            part.setReserveQuantity(part.getReserveQuantity() - 1);
                            part.setRemainingQuantity(part.getRemainingQuantity() + 1);
                            sparePartRepository.save(part);
                        }
                    }

                    workOrder.setCostOfSparePart(null);
                    workOrder.setCostOfWork(null);
                    workOrder.setSpareParts(null);
                    workOrder.setCompletedWorks(null);
                    workOrder.setCompletedAt(null);
                    workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                } else if (dto.getRepairStatus() == RepairStatus.COMPLETED) {
                    workOrder.setCompletedAt(LocalDateTime.now());
                    workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                } else if (dto.getRepairStatus() == RepairStatus.AT_WORK) {
                    workOrder.setCompletedAt(null);
                    workOrder.setPaymentStatus(PaymentStatus.UNPAID);
                }
            }
        }

        if (dto.getSparePartIds() != null) {
            List<SparePart> newParts = sparePartRepository.findAllById(dto.getSparePartIds());
            List<SparePart> oldParts = Optional.ofNullable(workOrder.getSpareParts()).orElse(List.of());

            Set<Long> newIds = newParts.stream().map(SparePart::getId).collect(Collectors.toSet());
            Set<Long> oldIds = oldParts.stream().map(SparePart::getId).collect(Collectors.toSet());

            Set<Long> removedIds = new HashSet<>(oldIds);
            removedIds.removeAll(newIds);

            Set<Long> addedIds = new HashSet<>(newIds);
            addedIds.removeAll(oldIds);

            for (SparePart part : oldParts) {
                if (removedIds.contains(part.getId())) {
                    part.setReserveQuantity(part.getReserveQuantity() - 1);
                    part.setRemainingQuantity(part.getRemainingQuantity() + 1);
                    sparePartRepository.save(part);
                }
            }

            for (SparePart part : newParts) {
                if (addedIds.contains(part.getId())) {
                    if (part.getRemainingQuantity() < 1) {
                        throw new SparePartUnavailableException(part.getBatchNumber());
                    }

                    part.setRemainingQuantity(part.getRemainingQuantity() - 1);
                    part.setReserveQuantity(part.getReserveQuantity() + 1);
                    sparePartRepository.save(part);
                }
            }

            workOrder.setSpareParts(new ArrayList<>(newParts));
            modified = true;
        }

        if (dto.getCompletedWorkIds() != null) {
            List<CompletedWork> works = completedWorkRepository.findAllById(dto.getCompletedWorkIds());
            workOrder.setCompletedWorks(new ArrayList<>(works));
            modified = true;
        }

        if (modified) {
            BigDecimal totalSparePartCost = Optional.ofNullable(workOrder.getSpareParts()).orElse(List.of())
                    .stream().map(SparePart::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalWorkCost = Optional.ofNullable(workOrder.getCompletedWorks()).orElse(List.of())
                    .stream().map(CompletedWork::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCost = totalWorkCost.add(totalSparePartCost);
            BigDecimal salary = totalWorkCost.multiply(BigDecimal.valueOf(workOrder.getPerformedBy().getShareOfWork()));

            workOrder.setCostOfSparePart(totalSparePartCost);
            workOrder.setCostOfWork(totalWorkCost);
            workOrder.setTotalCost(totalCost);
            workOrder.setSalary(salary);
        }

        return workOrderRepository.save(workOrder);
    }

    @Transactional(readOnly = true)
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

        return workOrderRepository.findAll(spec, pageable).map(workOrderMapper::toDto);
    }

    public Optional<WorkOrder> getByRepairRequestId(Long repairRequestId) {
        return workOrderRepository.findByRepairRequestId(repairRequestId);
    }

    public Optional<WorkOrder> getById(Long workOrderId) {
        return workOrderRepository.findById(workOrderId);
    }

}