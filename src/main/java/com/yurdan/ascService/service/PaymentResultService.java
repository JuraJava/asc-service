package com.yurdan.ascService.service;

import com.yurdan.ascService.model.entity.PaymentResult;
import com.yurdan.ascService.repository.PaymentResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Этот класс — это сервисный компонент, предназначенный для:
 * обработки информации о результатах платежей,
 * сохранения объекта PaymentResult в БД через репозиторий,
 * логирования операций сохранения.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentResultService {

    private final PaymentResultRepository paymentResultRepository;

    @Transactional
    public void savePaymentResult(PaymentResult result) {
        log.info("Сохраняем в БД payment_result: {}", result);
        paymentResultRepository.save(result);
    }
}
