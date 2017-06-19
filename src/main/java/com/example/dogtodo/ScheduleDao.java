package com.example.dogtodo;

import java.time.LocalDate;
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

    public List<Schedule> findAll(){
        return jdbc.query("SELECT * FROM schedule",new BeanPropertyRowMapper<>(Schedule.class));
    }
    
    public List<Map<String, Object>> findById(int id){
        return jdbc.queryForList("SELECT * FROM schedule WHERE id=?", id);
    }
    
    /*public List<Schedule> findDay(int dogtype, String title, LocalDate day){
        return jdbc.query("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogtype,title,day);
    }*/
}