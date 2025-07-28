package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long> {
}
