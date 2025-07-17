package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.RepairRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Этот интерфейс расширяет два интерфейса:
 * JpaRepository<RepairRequest, Long>: предоставляет стандартные методы (save(), findById(), findAll(), delete()) для CRUD и пагинации
 * JpaSpecificationExecutor<RepairRequest>: позволяет выполнять более сложные запросы с использованием
 * Specifications — это паттерн, позволяющий строить динамические запросы с условием.
 *
 */
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long>, JpaSpecificationExecutor<RepairRequest> {

    /**
     * Это метод Spring Data JPA с названием, из которого он сам сгенерирует запрос, ищет запись в таблице
     * repair_request по значению поля serialNumber, возвращает Optional<RepairRequest>
     * если запись найдена — в Optional будет объект RepairRequest и
     * если записи нет — Optional будет пустым, что позволяет избежать NullPointerException и делать удобные проверки.
     */
    Optional<RepairRequest> findBySerialNumber(String serialNumber);
}


