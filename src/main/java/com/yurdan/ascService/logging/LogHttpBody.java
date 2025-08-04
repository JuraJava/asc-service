package com.yurdan.ascService.logging;

import java.lang.annotation.*;

/**
 * Это  собственная, пользовательская аннотация для пометки методов,
 * где нужно логировать тело запроса/ответа/ошибок.
 * Доступна во время выполнения.
 * Будет попадать в Javadoc (@Documented)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogHttpBody {
}