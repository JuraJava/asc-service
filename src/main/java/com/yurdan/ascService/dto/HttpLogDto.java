package com.yurdan.ascService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для логирования
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpLogDto {
    private String url;
    private int httpStatus;
    private String responseBody;
}
