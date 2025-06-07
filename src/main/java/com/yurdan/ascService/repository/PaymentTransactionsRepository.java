package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionsRepository extends JpaRepository<PaymentTransactions, Long> {

}
