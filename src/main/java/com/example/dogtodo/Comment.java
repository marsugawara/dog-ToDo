package com.example.dogtodo;

import java.time.LocalDate;

import lombok.Data;


@Data
public class Comment {

    private int id;
    private LocalDate day;
    private String text;

}
