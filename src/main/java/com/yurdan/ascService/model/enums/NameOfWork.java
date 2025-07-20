package com.yurdan.ascService.model.enums;

public enum NameOfWork {
    DIAGNOSTIC("Диагностика"),
    PROGRAM_REPAIR("Программный ремонт"),
    REPLACEMENT_ON_PAID_BASIS("Замена на платной основе"),
    ADDITIONAL_WORK("Дополнительная работа"),
    UNBLOCKING("Разблокировка"),
    REPLACEMENT_UNDER_WARRANTY("Замена по гарантии");

    private String value;

    NameOfWork(String value) {
        this.value = value;
    }
}
