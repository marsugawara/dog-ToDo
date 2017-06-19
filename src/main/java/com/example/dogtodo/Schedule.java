package com.example.dogtodo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Schedule {

    private int id;
    private int dogtyoe;
    private String title;
    private int endcheck;
    private LocalDate day;
    private LocalDateTime checktime;

}
