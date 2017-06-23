package com.example.dogtodo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Comment> findCommentDay(LocalDate day){
        return jdbc.query("SELECT text FROM comment WHERE day=?",new BeanPropertyRowMapper<>(Comment.class),day);
    }

    public List<Comment> findComment(){
        return jdbc.query("SELECT day FROM comment",new BeanPropertyRowMapper<>(Comment.class));
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
