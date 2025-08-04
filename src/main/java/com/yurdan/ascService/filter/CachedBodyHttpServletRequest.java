package com.yurdan.ascService.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;

/**
 * Это класс - обёртка над HttpServletRequest, которая кэширует тело запроса (body),
 * чтобы его можно было прочитать многократно. По умолчанию тело HTTP-запроса можно прочитать
 * только один раз, а потом поток закрывается.
 * Этот класс можно использовать внутри Filter и тогда после этого все классы (аспекты, контроллеры),
 * которые читают getInputStream() или
 * getReader(), будут читать тело запроса из памяти, а не из потока.
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    /**
     * Конструктор, который:
     * Вызывает super(request) — сохраняет оригинальный HttpServletRequest.
     * Читает тело запроса один раз: request.getInputStream().readAllBytes().
     * Сохраняет его в cachedBody.
     * После этого оригинальный request.getInputStream() будет недоступен (данные уже прочитаны).
     * Поэтому все последующие вызовы getInputStream() и getReader() будут идти через кэш.
     */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = requestInputStream.readAllBytes();
    }

    /**
     * Возвращает поток, который читает из кэшированного массива байтов,
     * а не из оригинального запроса
     */
    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedBody);
    }

    /**
     * Делает то же самое, что и getInputStream(), но возвращает
     * BufferedReader — удобный способ читать текст (JSON, XML и др.).
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * Этот вложенный класс - это обёртка над ByteArrayInputStream,
     * позволяющая Spring/Servlet API читать как будто это обычный ServletInputStream
     */
    private static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        /**
         * Конструктор, создаёт поток, читающий из массива байтов
         */
        public CachedServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        /**
         * Позволяет читать байты по одному — реализация абстрактного метода.
         */
        @Override
        public int read() {
            return inputStream.read();
        }

        /**
         * Возвращает true, если всё содержимое уже прочитано
         */
        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        /**
         * Возвращает true — поток всегда "готов" к чтению.
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * Здесь просто заглушка — асинхронное чтение не используется.
         */
        @Override
        public void setReadListener(ReadListener readListener) {
            // not used
        }
    }
}
