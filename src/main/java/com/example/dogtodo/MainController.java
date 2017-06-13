package com.example.dogtodo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.Value;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;

    /*
     * @GetMapping("/test") public String test(){
     * System.out.println(jdbc.queryForList(
     * "SELECT dogtype,title FROM schedule WHERE dogtype=0"));
     * System.out.println(jdbc.queryForList(
     * "SELECT dogtype,title FROM schedule WHERE dogtype=1 AND day IS null"));
     * return "indent"; }
     */

    // 使うやつ
    private List<Goods> rokuta;
    private List<Goods> nanako;

    // 処理の中身
    @GetMapping("/indent") // 全体の初期ページ
    public String hello(Goods goods, Model model) {
        printList(model);
        printComment(model);
        return "indent";
    }

    @GetMapping("/rokuplus") // ろくた追加ボタン
    public String rokuplus(String item, Model model) {
        addDay(0, item, new Date());
        printList(model);
      //  printComment(model);
        return "indent";
    }

    @GetMapping("/nanaplus") // ななこ追加ボタン
    public String nanaplus(String item, Model model) {
        addDay(1, item, new Date());
        printList(model);
      //  printComment(model);
        return "indent";
    }

    @GetMapping("/form") // コメント書込ボタン
    public String sample(String comm, Model model) {
        addComment(LocalDate.now(), comm);
        printList(model);
        printComment(model);
        return "indent";
    }

    // メソッド作成
    public void printList(Model model) { // チェックボックスのメソッド
        rokuta = new ArrayList<Goods>(); // rokutaのリスト作成
        List<Map<String, Object>> rokutaSQL = jdbc.queryForList("SELECT title,checktime FROM schedule WHERE dogtype=0");
        for (int i = 0; i < rokutaSQL.size(); i++) {
            String checktime;
            if ((rokutaSQL.get(i)).get("checktime") == null) {
                checktime = "";
            } else {
                checktime = (rokutaSQL.get(i)).get("checktime").toString();
            }
            rokuta.add(new Goods((rokutaSQL.get(i)).get("title").toString(), checktime));
        }

        nanako = new ArrayList<Goods>(); // nanakoのリスト作成
        List<Map<String, Object>> nanakoSQL = jdbc.queryForList("SELECT title,checktime FROM schedule WHERE dogtype=1");
        for (int i = 0; i < nanakoSQL.size(); i++) {
            String checktime;
            if ((nanakoSQL.get(i)).get("checktime") == null) {
                checktime = "";
            } else {
                checktime = (nanakoSQL.get(i)).get("checktime").toString();
            }
            nanako.add(new Goods((nanakoSQL.get(i)).get("title").toString(), checktime));
        }

        model.addAttribute("rokutaGohans", rokuta); // 表示するために渡してる
        model.addAttribute("nanakogohans", nanako); // 表示するために渡してる
    }
    //コメント表示
    
    public void printComment(Model model){
        String textcomm = "";
        
        List<Map<String, Object>>text_comment = jdbc.queryForList(
               "SELECT text FROM comment WHERE day=day");
        if(text_comment.size() != 0){
            textcomm = (text_comment.get(0)).get("text").toString();
        }
        model.addAttribute("comm",textcomm);
        
    }
    
    
    // 基本項目の追加
    public void addDay(int dogType, String title, Date day) {
        jdbc.update("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogType,title,day);
    }

    @GetMapping("/test")
    public String selectCheck(Model model){
        System.out.println("check");
        printList(model);
        return "indent";
    }

    // コメントの追加
    public void addComment(LocalDate day, String text){
        List<Map<String, Object>> comm_daySQL = jdbc.queryForList("SELECT day FROM comment");
        
        int k = 0;
        for (int i = 0; i < comm_daySQL.size(); i++){
            LocalDate date = LocalDate.parse((comm_daySQL.get(i)).get("day").toString());
            if (date.equals(day)) {
                k = 1;
                break;
            }else{
                k = 0;
            }
        }

        switch(k){
            case 0:
                jdbc.update("INSERT INTO comment (text, day)"
                        + " VALUES (?, ?)",text, LocalDate.now());
               // System.out.println("0"+ LocalDate.now());
                break;
            case 1:
                jdbc.update("UPDATE comment" 
                        + " SET text = ?"
                        + "WHERE day = ?",text, LocalDate.now());
                System.out.println("1"+ LocalDate.now());
                break;
        }
    }
}
