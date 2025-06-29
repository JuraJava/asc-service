package com.yurdan.ascService.repository;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.dto.DeviceSelectDto;
import com.yurdan.ascService.model.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("""
           SELECT new com.yurdan.ascService.dto.DeviceSelectDto(d.id, CONCAT(d.deviceName, ' ', d.deviceColor))
           FROM Device d
           WHERE LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(d.deviceColor) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(CONCAT(d.deviceName, ' ', d.deviceColor)) LIKE LOWER(CONCAT('%', :name, '%'))
           """)
    List<DeviceSelectDto> findByPartOfName(String name);

    @Query("""
           SELECT new com.yurdan.ascService.dto.DeviceDto(d.id, d.deviceName, d.deviceColor)
           FROM Device d
           """)
    Page<DeviceDto> findAllByPageable(Pageable pageable);
}
