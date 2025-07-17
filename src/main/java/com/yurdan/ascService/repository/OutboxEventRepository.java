package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.OutboxEvent;
import com.yurdan.ascService.model.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *Аннотация помечает этот интерфейс как компонент репозитория,
 * позволяет Spring обнаружить этот бин и управлять им (например, обработка исключений БД автоматически),
 * в случае использования Spring Data JPA эта аннотация опциональна,
 * т.к. Spring сам распознает интерфейсы, расширяющие JpaRepository, но обячно добавляется для явности.
 * Это интерфейс репозитория, который управляет сущностями типа OutboxEvent,
 * JpaRepository предоставляет базовые CRUD-операции и дополнительные возможности для работы с БД.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    /**
     * Метод позволяет получить несколько новых событий для последующей отправки другому сервису.
     * Сортировка по времени гарантирует, что события обрабатываются в порядке их создания.
     */
    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}