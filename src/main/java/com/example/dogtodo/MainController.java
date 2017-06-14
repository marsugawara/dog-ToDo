package com.example.dogtodo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;



    // 使うやつ
    private List<Goods> rokuta;
    private List<Goods> nanako;

    // 処理の中身
    @GetMapping("/indent") // 全体の初期ページ
    public String hello(LocalDate date, Goods goods, Model model) {
        if (date == null) {
            model.addAttribute("rokutaGohan", getGoods(0, LocalDate.now()));
            model.addAttribute("nanakogohan", getGoods(1, LocalDate.now()));
        } else {
            model.addAttribute("rokutaGohan", getGoods(0, date));
            model.addAttribute("nanakogohan", getGoods(1, date));
        }

       // printComment(model);

        model.addAttribute("currentDate", date);

        return "indent";
    }

    @PostMapping("/test")
    public String test(LocalDate date, RedirectAttributes attr) {

        System.out.println(date);

        attr.addFlashAttribute("date", date);

        return "redirect:/indent";
    }

    @PostMapping("/rokuplus") // ろくた追加ボタン
    public String rokuplus(String item, Model model, RedirectAttributes attr) {
        addDay(0, item, new Date());
        printList(model);
      //  printComment(model);
        return "redirect:/indent";
    }

    @PostMapping("/nanaplus") // ななこ追加ボタン
    public String nanaplus(String item, Model model, RedirectAttributes attr) {
        addDay(1, item, new Date());
        printList(model);
      //  printComment(model);
        return "redirect:/indent";
    }

    @PostMapping("/form") // コメント書込ボタン
    public String sample(String comm, Model model, RedirectAttributes attr) {
        addComment(LocalDate.now(), comm);
        printList(model);
        printComment(model);
        return "redirect:/indent";
    }
    // ななこチェック後
    /*
    @GetMapping("/na_aftercheck")
    public String selectCheck(Model model){
        addChecktime(LocalDateTime.now(),1);
        printList(model);
        return "indent";
    }*/

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

        model.addAttribute("rokutaGohans", rokuta); // 表示するために渡してる
    }

    private List<Goods> getGoods(int dogType, LocalDate date) {
        List<Map<String, Object>> schedules = jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ?", dogType, date);
        List<Goods> goods = new ArrayList<>();

        for (Map<String, Object> schedule : schedules) {
            String checktime;
            if ((schedule).get("checktime") == null) {
                checktime = "";
            } else {
                checktime = (schedule).get("checktime").toString();
            }
            goods.add(new Goods((schedule).get("title").toString(), checktime));
        }

        return goods;
    }

    //コメント表示
    public void printComment(Model model){
        String textcomm = "";
        
        List<Map<String, Object>>text_comment = jdbc.queryForList(
               "SELECT text FROM comment WHERE day=date");
        if(text_comment.size() != 0){
            textcomm = (text_comment.get(0)).get("text").toString();
        }
        model.addAttribute("comm",textcomm);
        
    }


    // 基本項目の追加
    public void addDay(int dogtype, String title, Date day) {
        jdbc.update("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogtype,title,day);
    }
    // チックされた時間の追加
    /*
    public void addChecktime(LocalDateTime day,int dogtype){
        
        
        
        
        jdbc.update("UPDATE schedule" 
                + " SET checktime = ?"
                + "WHERE dogtype=?"
                + "AND day=day"
                + "AND title=title", LocalDateTime.now(),dogtype);
    }*/

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
