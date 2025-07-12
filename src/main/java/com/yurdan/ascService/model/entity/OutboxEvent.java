package com.yurdan.ascService.model.entity;

import com.yurdan.ascService.model.enums.OutboxEventType;
import com.yurdan.ascService.model.enums.OutboxEventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType; // Например: "PaymentTransaction"

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId; // Например: ID платежа

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload; // JSON

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}