package com.yurdan.ascService.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Этот класс  — глобальный обработчик исключений в Spring Boot приложении. Он обеспечивает централизованную обработку
 * всех ошибок, возникающих при выполнении REST-запросов, и формирует удобные и понятные ответы клиенту,
 * вместо стандартных Java-ошибок. @RestControllerAdvice - делает этот класс глобальным обработчиком
 * ошибок для всех контроллеров в приложении, все ответы возвращаются в формате JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Вызывается, если не удалось прочитать/распарсить тело запроса.
     * Обрабатываются случаи:
     * UnrecognizedPropertyException – неизвестное поле в JSON.
     * InvalidFormatException – неправильный формат поля.
     * Возвращает: 400 Bad Request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "The field name is incorrect. Enter the field name correctly!"
            ));
        } else if (cause instanceof InvalidFormatException) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "The format of one of the fields is incorrect. Check your data!"
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "error", "The data could not be uploaded. We are already working on a solution to this problem!"
        ));
    }
    /**
     * Вызывается если метод не поддерживается, например, вызван POST, а поддерживается только GET.
     * Возвращает: 405 Method Not Allowed.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
                "error", "The method is specified incorrectly. Specify the correct method!"
        ));
    }
    /**
     * Вызывается если ошибки валидации данных (например, пустое поле, неверная длина).
     * Возвращает: 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "The data could not be uploaded. We are already working on a solution to this problem!"
        ));
    }
    /**
     * Вызывается при ошибках валидации на уровне параметров, например, проверка параметра запроса: @Size(min=3)
     * Собирает ошибки в Map<field, message> и отправляет их клиенту.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.badRequest().body(errors);
    }
    /**
     * Обрабатывает любые ошибки подключения, транзакций и запросов к БД.
     * Возвращает: 500 Internal Server Error.
     */
    @ExceptionHandler({
            SQLException.class,
            DataAccessException.class,
            JDBCConnectionException.class,
            CannotCreateTransactionException.class,
            PersistenceException.class,
            JpaSystemException.class
    })
    public ResponseEntity<Map<String, String>> handleDatabaseException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error connecting to the database. Please try again later!"
        ));
    }

    /**
     * Общий обработчик для всех остальных исключений
     * Ловит все непредвиденные ошибки, которые не были перехвачены ранее.
     * Выводит стек ошибки в консоль: ex.printStackTrace().
     * Разделяет:
     * Пользовательские ошибки (например, EmployeeNotFoundException) – возвращает 400 с сообщением.
     * Непредвиденные ошибки – возвращает 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // <-- Добавлен вывод стектрейса в консоль

        Set<Class<?>> badRequestExceptions = Set.of(
                DeviceNotFoundException.class,
                EmployeeNotFoundException.class,
                EngineerNotFoundException.class,
                EngineerRoleRequiredException.class,
                NoPaymentForWarrantyRepairs.class,
                NotAllWorkOrdersClosedException.class,
                NoWorkOrdersForRepairRequestException.class,
                PaymentTransactionAlreadyExistsException.class,
                ProhibitionCreateOrderBasedOnClosedOOrOnPaymentRequestException.class,
                RepairRequestNotFoundException.class,
                RepairStatusViolationException.class,
                SparePartUnavailableException.class,
                UnauthorizedActionException.class,
                UnauthorizedWorkOrderUpdateException.class,
                WorkOrderNotFoundException.class
        );

        if (badRequestExceptions.contains(ex.getClass())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", ex.getMessage()
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "An unexpected error occurred. We are already working on a solution!"
        ));
    }
}