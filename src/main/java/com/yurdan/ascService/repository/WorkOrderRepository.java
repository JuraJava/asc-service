package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {
    Optional<WorkOrder> findByRepairRequestId(Long repairRequestId);
}
