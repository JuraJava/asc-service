package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Этот интерфейс наследуется от:
 * JpaRepository<WorkOrder, Long> — стандартный интерфейс, предоставляющий готовые CRUD-методы (save(), findById(), findAll(), delete() и другие),
 * JpaSpecificationExecutor<WorkOrder> — позволяет использовать спецификации (гибкие фильтры), например, при построении запросов с условиями по нескольким полям.
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {
    /**
     * Этот метод ищет WorkOrder по ID заявки на ремонт (repairRequestId).
     * Возвращает результат в Optional, что позволяет избежать null и безопасно обрабатывать отсутствие записи.
     */
    Optional<WorkOrder> findByRepairRequestId(Long repairRequestId);
}
