package com.yurdan.ascService.model.enums;

import lombok.Getter;

@Getter
public enum TypeOfRepair {
    WARRANTY("Гарантия"),
    NON_WARRANTY("Платный");

    private final String value;

    TypeOfRepair(String value) {
        this.value = value;
    }
}
