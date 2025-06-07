package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.RepairRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long>, JpaSpecificationExecutor<RepairRequest> {
    Optional<RepairRequest> findBySerialNumber(String serialNumber);
}


