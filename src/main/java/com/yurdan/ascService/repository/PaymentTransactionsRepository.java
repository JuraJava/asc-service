package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Это интерфейс репозитория, управляющий сущностями PaymentTransactions.
 * Расширяет JpaRepository, которая предоставляет множество стандартных методов:
 * save(), findById(), findAll(), delete(), и др.
 * Long — тип идентификатора (ID) сущности PaymentTransactions.
 */
public interface PaymentTransactionsRepository extends JpaRepository<PaymentTransactions, Long> {
    boolean existsByRepairRequestId(Long repairRequestId);
}
