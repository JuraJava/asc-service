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

/**
 * Это сервисный класс, который отвечает за создание, обновление и поиск заказов на ремонт (WorkOrder).
 * Используются различные Entity, DTO, репозитории (DAO) и мапперы для преобразования между Entity и DTO.
 * Применены Spring Data JPA, транзакции, спецификации для динамического фильтра поиска и кастомные исключения.
 */
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

    /**
     * Создаётся новый заказ на ремонт на основе заявки.
     * Получаем сотрудника по performedById, проверяем, что он инженер (RoleOfEmployee.ENGINEER), иначе бросаем исключение.
     * Получаем заявку на ремонт по dto.getRepairRequestId(), если не нашли — исключение.
     * Проверяем статус заявки: нельзя создавать заказ если заявка "на оплате" (ON_PAYMENT) или "закрыта" (CLOSED).
     * Обновляем статус заявки на AT_WORK (в работе).
     * Получаем списки выполненных работ и запчастей по ID из DTO.
     * Для каждой запчасти проверяем, что количество на складе больше 0, иначе — исключение SparePartUnavailableException.
     * Обновляем количество запчастей: уменьшаем количество на складе и увеличиваем резерв.
     * Считаем суммарную стоимость работ и запчастей.
     * Рассчитываем зарплату инженера по формуле: стоимость работы * доля инженера.
     * Создаём объект WorkOrder, заполняем поля, сохраняем в БД.
     */
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

    /**
     * Получаем сотрудника-редактора и заказ по ID, проверяем их существование.
     * Если заказ закрыт (RepairStatus.CLOSED), запрещаем редактирование — выбрасываем исключение.
     * Проверяем, является ли редактор инженером, который выполняет заказ, или сотрудником с привилегиями (приёмщик, администратор).
     * Если нет прав — исключение.
     * Вызываем защищённый метод updateWorkOrderTransactional для выполнения обновления в рамках транзакции.
     */
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

    /**
     * Здесь происходит вся логика обновления заказа.
     * Обновление инженера, который выполняет заказ, с проверками роли.
     * Изменение статуса заказа с соблюдением бизнес-логики:
     * Закрыть заказ можно только если он был в статусе COMPLETED.
     * Только привилегированные сотрудники могут закрыть заказ.
     * При закрытии автоматически меняется статус оплаты на PAID и уменьшается резерв запчастей.
     * Если заказ отменяется (CANCELLED), возвращаем запчасти на склад, очищаем списки работ и деталей, обнуляем стоимость и статус оплаты.
     * Для статусов COMPLETED и AT_WORK устанавливаем или сбрасываем дату завершения и статус оплаты.
     * Обработка изменения списка запчастей:
     * Находим какие запчасти были удалены или добавлены.
     * Для удалённых запчастей уменьшаем резерв и увеличиваем количество на складе.
     * Для новых — проверяем наличие на складе, уменьшаем количество и увеличиваем резерв.
     * Обновление списка выполненных работ.
     * Пересчёт стоимости работ, деталей, итоговой суммы и зарплаты инженера, если что-то изменилось.
     * Сохраняем заказ в БД и возвращаем.
     */
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

                if (!isPrivileged) {
                    throw new UnauthorizedWorkOrderUpdateException("Только привилегированный персонал может закрыть заказ");
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

    /**
     * Метод для получения страниц заказов с фильтрацией по дате завершения, статусам ремонта и оплаты, ID заявки и ID исполнителя.
     * Формируется динамическая спецификация (условия поиска) с помощью Specification для JPA.
     * Возвращается страница DTO заказов.
     */
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

    /**
     * Возвращает заказ по ID заявки на ремонт.
     */

    public List<WorkOrder> getAllByRepairRequestId(Long repairRequestId) {
        return workOrderRepository.findAllByRepairRequestId(repairRequestId);
    }

    /**
     * Возвращает заказ по ID самого заказа (Optional).
     */
    public Optional<WorkOrder> getById(Long workOrderId) {
        return workOrderRepository.findById(workOrderId);
    }
}