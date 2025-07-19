package com.yurdan.ascService.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Этот класс отвечает за автоматическое управление партициями (разделами) таблицы outbox_event в PostgreSQL.
 * Это часть реализации Outbox Pattern, где данные организуются по
 * дням — для производительности и удобного удаления старых записей.
 * retentionDays — число дней, сколько хранить партиции, которое указывается в application.yml
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPartitionScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Value("${outbox.partition.retention-days}")
    private int retentionDays;

    // Запускается каждый день в 00:05
    @Scheduled(cron = "0 5 0 * * *")
    public void manageOutboxPartitions() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dropBefore = today.minusDays(retentionDays);

        String todayPartition = today.toString().replace("-", "_");
        String dropPartition = dropBefore.toString().replace("-", "_");

        String createPartitionSql = String.format("""
            CREATE TABLE IF NOT EXISTS outbox_event_%s
            PARTITION OF outbox_event
            FOR VALUES FROM ('%s 00:00:00') TO ('%s 00:00:00');
        """, todayPartition, today, tomorrow);

        String dropPartitionSql = String.format("""
            DROP TABLE IF EXISTS outbox_event_%s CASCADE;
        """, dropPartition);

        try {
            // Создание партиции
            jdbcTemplate.execute(createPartitionSql);

            if (partitionAlreadyExists(todayPartition)) {
                log.info("Партиция уже существует и была пропущена: outbox_event_{}", todayPartition);
            } else {
                log.info("Создана партиция: outbox_event_{}", todayPartition);
            }

            // Удаление устаревшей
            jdbcTemplate.execute(dropPartitionSql);
            log.info("Удалена устаревшая партиция: outbox_event_{}", dropPartition);
        } catch (Exception e) {
            log.error("Ошибка создания/удаления партиций outbox_event", e);
        }
    }

    /**
     * Этот метод проверяет, существует ли партиция в базе.
     */
    private boolean partitionAlreadyExists(String partitionSuffix) {
        try {
            String checkSql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM pg_tables
                    WHERE tablename = ?
                )
            """;
            Boolean exists = jdbcTemplate.queryForObject(checkSql, Boolean.class, "outbox_event_" + partitionSuffix);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("Не удалось проверить существование партиции: outbox_event_{}", partitionSuffix, e);
            return false;
        }
    }

}
