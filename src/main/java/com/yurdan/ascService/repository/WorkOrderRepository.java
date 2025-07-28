package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Этот интерфейс наследуется от:
 * JpaRepository<WorkOrder, Long> — стандартный интерфейс, предоставляющий готовые CRUD-методы (save(), findById(), findAll(), delete() и другие),
 * JpaSpecificationExecutor<WorkOrder> — позволяет использовать спецификации (гибкие фильтры), например, при построении запросов с условиями по нескольким полям.
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    /**
     * Этот метод ищет WorkOrder по ID заявки на ремонт (repairRequestId)
     */
    List<WorkOrder> findAllByRepairRequestId(Long repairRequestId);

    /**
     * с JOIN FETCH
     */
    @Query("""
    SELECT wo FROM WorkOrder wo
    LEFT JOIN FETCH wo.completedWorks
 
    WHERE wo.id = :id
""")
    Optional<WorkOrder> findByIdWithDetails(@Param("id") Long id);

    @Query("""
    SELECT wo FROM WorkOrder wo
    LEFT JOIN FETCH wo.completedWorks
    WHERE wo.repairRequest.id = :repairRequestId
""")
    List<WorkOrder> findAllByRepairRequestIdWithDetails(@Param("repairRequestId") Long repairRequestId);

}
