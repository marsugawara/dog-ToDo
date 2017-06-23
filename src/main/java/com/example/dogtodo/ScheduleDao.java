package com.example.dogtodo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Schedule> checkFlag(int id){
        return jdbc.query("SELECT checktime FROM schedule WHERE id = ?",
                new BeanPropertyRowMapper<>(Schedule.class),id);
    }

    public void checkInsert(DogType dogType, Title title, String day, LocalDateTime checkTime){
        jdbc.update("INSERT INTO schedule (dogtype, title, day, checktime)"
                + " VALUES(?, ?, ?, ?)", dogType.getValue(), title.getValue(), day, checkTime);
    }

    public void checkUpdate(LocalDateTime checkTime, int id){
        jdbc.update("UPDATE schedule "
                + "SET checktime = ?"
                + "WHERE id = ?", checkTime, id);
    }

    public List<Schedule> findScheduleByDogTypeDayAndTitle(DogType dogType, LocalDate day, Title title){
        return jdbc.query("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title = ?",
                new BeanPropertyRowMapper<>(Schedule.class), dogType.getValue(), day, title.getValue());
    }

    public Schedule findScheduleByBreakfastId(DogType dogType){
        return jdbc.queryForObject("SELECT * FROM schedule WHERE id = ?",
                new BeanPropertyRowMapper<>(Schedule.class), dogType.getValue()*2 + 1);
    }

    public Schedule findScheduleByDinnerId(DogType dogType){
        return jdbc.queryForObject("SELECT * FROM schedule WHERE id = ?",
                new BeanPropertyRowMapper<>(Schedule.class), (dogType.getValue()+1)*2);
    }

    public List<Schedule> findScheduleByDogTypeAndDay(DogType dogType, LocalDate day, Title breakfast, Title dinner){
        return jdbc.query("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title NOT IN (?, ?)",
                new BeanPropertyRowMapper<>(Schedule.class), dogType.getValue(), day, breakfast.getValue(), dinner.getValue());
    }

    public void addDay(int dogType, String title, LocalDate day){
        jdbc.update("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogType,title,day);
    }



}