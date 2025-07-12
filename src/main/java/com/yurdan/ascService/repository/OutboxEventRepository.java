package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.OutboxEvent;
import com.yurdan.ascService.model.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
//    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
//}

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}