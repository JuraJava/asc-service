package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

}
