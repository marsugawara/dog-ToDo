package com.example.dogtodo;

import lombok.Getter;

public enum Title {

    BREAKFAST("朝ごはん"),
    DINNER("夜ごはん");

    @Getter
    private String value;

    private Title(String value) {
        this.value = value;
    }
}
