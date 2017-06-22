package com.example.dogtodo;

import lombok.Getter;

public enum DogType {

    ROKUTA(0),
    NANAKO(1);
    
    @Getter
    private int value;
    
    private DogType(int value){
        this.value = value;
    }
    
    public static DogType of(int value) {
        for (DogType dogType : DogType.values()) {
            if (dogType.getValue() == value) {
                return dogType;
            }
        }

        throw new RuntimeException("定義域の範囲外の値が入力されました。");
    }
}
