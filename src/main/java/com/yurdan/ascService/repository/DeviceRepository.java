package com.yurdan.ascService.repository;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.dto.DeviceSelectDto;
import com.yurdan.ascService.model.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Этот интерфейс DeviceRepository — это репозиторий Spring Data JPA, который управляет сущностями Device.
 * Он расширяет JpaRepository<Device, Long>, а значит предоставляет базовые
 * CRUD-операции (save, findById, delete и т.п.) автоматически.
 * помимо базовых, в этом репозитории объявлены два пользовательских запроса с помощью аннотации @Query.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Поиск по части имени
     * Выполняет JPQL-запрос, ищет устройства, где часть текста встречается в:
     * названии устройства (deviceName), цвете устройства (deviceColor) или в их сочетании "deviceName deviceColor",
     * регистр игнорируется (LOWER()), возвращает список объектов DeviceSelectDto, содержащих:
     * id, deviceName + " " + deviceColor как одну строку
     */
    @Query("""
           SELECT new com.yurdan.ascService.dto.DeviceSelectDto(d.id, CONCAT(d.deviceName, ' ', d.deviceColor))
           FROM Device d
           WHERE LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(d.deviceColor) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(CONCAT(d.deviceName, ' ', d.deviceColor)) LIKE LOWER(CONCAT('%', :name, '%'))
           """)
    List<DeviceSelectDto> findByPartOfName(String name);

    /**
     * Возвращает страничный (Page<DeviceDto>) список всех устройств, каждое устройство маппится на DeviceDto,
     * использует DTO-проекцию — то есть выбирает только нужные поля, не всю сущность,
     * вернёт первые 10 устройств как DeviceDto
     */
    @Query("""
           SELECT new com.yurdan.ascService.dto.DeviceDto(d.id, d.deviceName, d.deviceColor)
           FROM Device d
           """)
    Page<DeviceDto> findAllByPageable(Pageable pageable);
}
