package com.example.dogtodo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Map<String, Object>> findCommentDay(LocalDate day){
        return jdbc.queryForList("SELECT text FROM comment WHERE day=?",day);
    }

    public List<Map<String, Object>> findComment(){
        return jdbc.queryForList("SELECT day FROM comment");
    }

    public void commInsert(String text, LocalDate day){
        jdbc.update("INSERT INTO comment (text, day)"
                + " VALUES (?, ?)",text, day);
    }

    public void commUpdate(String text, LocalDate day){
        jdbc.update("UPDATE comment" 
                + " SET text = ?"
                + "WHERE day = ?",text, day);
    }
}
