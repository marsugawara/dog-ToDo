package com.example.dogtodo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ScheduleDao scheduleDao;

    @GetMapping("/test")
    public String test(){
        System.out.println(scheduleDao.findAll());
        System.out.println(scheduleDao.findById(3));
        return "";
    }

    // 処理の中身
    @GetMapping("/indent") // 全体の初期ページ
    public String hello(String date, Goods goods, Model model) {
        LocalDate localDate = LocalDate.now();

        return "redirect:/indent:" + localDate;
    }
    
    @GetMapping("/indent:{date}")
    public String helloworld(@PathVariable("date") String date, Model model){
       // System.out.println(date);   //入ってきた日付の確認
      //  System.out.println(date == null || date.equals(""));
        if (date == null || date.equals("")){ 
            model.addAttribute("rokutaGohans", getGoods(0, LocalDate.now()));
            model.addAttribute("nanakogohans", getGoods(1, LocalDate.now()));
            model.addAttribute("date", LocalDate.now());
        } else {
            model.addAttribute("rokutaGohans", getGoods(0, LocalDate.parse(date)));
            model.addAttribute("nanakogohans", getGoods(1, LocalDate.parse(date)));
            model.addAttribute("date", LocalDate.parse(date));
        }
        printComment(LocalDate.parse(date), model);
      //  model.addAttribute("currentDate", date);

        return "indent";
    }

    @PostMapping("/yesterday")
    public String yesterdays(RedirectAttributes attr){
        LocalDate day = LocalDate.now().minusDays(1);

        attr.addFlashAttribute("date", day);
        return "redirect:/indent:" + day;
    }

    @PostMapping("/today")
    public String todays(RedirectAttributes attr){
        LocalDate day = LocalDate.now();

        attr.addFlashAttribute("date", day);
        return "redirect:/indent:" + day;
    }

    @PostMapping("/tomorrow")
    public String tomorrow(RedirectAttributes attr){
        LocalDate day = LocalDate.now().plusDays(1);

        attr.addFlashAttribute("date", day);
        return "redirect:/indent:" + day;
    }

    @PostMapping("/calendar")
    public String calendarSelect(String num01, RedirectAttributes attr){
        LocalDate day = LocalDate.parse(num01);

        attr.addFlashAttribute("date", day);
        return "redirect:/indent:" + day;
    }

    @PostMapping("/rokuplus") // ろくた追加ボタン
    public String rokuplus(String item, String catchDate, RedirectAttributes attr) {
        //addDay(0, item, LocalDate.now());
        LocalDate day = LocalDate.now();
        if(catchDate.equals(null) == false && catchDate.equals("") == false){
            day = LocalDate.parse(catchDate);
        }
        addDay(0, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/indent:" + day;
    }

    @PostMapping("/nanaplus:{date}") // ななこ追加ボタン
    public String nanaplus(@PathVariable("date") String date, String item, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
       // System.out.println(date);
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }
        addDay(1, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/indent:" + day;
    }
    // ななこチェック後
    @PostMapping("/na_aftercheck:{date}")
    public String selectCheck(@PathVariable("date") String date, RedirectAttributes attr){
       // System.out.println("checkbox: " + date);
        LocalDateTime time = LocalDateTime.now();
        addChecktime(date,time,1);
        //attr.addFlashAttribute();
        
        return "redirect:/indent:" + date;
    }

    // チックされた時間の追加
    public void addChecktime(String date, LocalDateTime time, int dogtype){
        
        
        
        
        jdbc.update("UPDATE schedule" 
                + " SET checktime = ?"
                + "WHERE dogtype=?"
                + "AND day=?"
                + "AND title=title",time, dogtype, date);
        System.out.println(time);
    }
    
    
    @PostMapping("/comment") // コメント書込ボタン
    public String sample(CommentForm form, String catchDate, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(catchDate.equals(null) == false && catchDate.equals("") == false){
            day = LocalDate.parse(catchDate);
        }

        addComment(day, form.getComm());
        attr.addFlashAttribute(form.getComm());

        return "redirect:/indent:" + day;
    }



    // チェックリストの作成
    private List<Goods> getGoods(int dogType, LocalDate date) {
        List<Map<String, Object>> schedules = jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ?", dogType, date);
        List<Goods> goods = new ArrayList<Goods>();

        /*for (Map<String, Object> schedule : schedules) {
            if(schedule.get("title").toString().equals("朝ごはん") && schedule.get("checktime").toString().equals(null) == false){
                schedules.remove(0);
            }
            
        }*/
        for (Map<String, Object> schedule : schedules) {
            String checktime;
            if ((schedule).get("checktime") == null) {
                checktime = "";
            } else {
                //時分秒のみ表示する
                //System.out.println((schedule).get("checktime"));
                checktime = (schedule).get("checktime").toString();
                checktime = checktime.split(" ")[1];
                checktime = checktime.substring(0, checktime.indexOf("."));
                System.out.println("checktime: " + checktime);
            }
            goods.add(new Goods((schedule).get("title").toString(), checktime));
        }

        return goods;
    }

    //コメント表示
    public void printComment(LocalDate day, Model model){
        String textcomm = "";
        
        List<Map<String, Object>>text_comment = jdbc.queryForList("SELECT text FROM comment WHERE day=?",day);
        if(text_comment.size() != 0){
            textcomm = (text_comment.get(0)).get("text").toString();
        }
        model.addAttribute("comm",textcomm);
        
    }


    // 基本項目の追加
    public void addDay(int dogtype, String title, LocalDate day) {
        jdbc.update("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogtype,title,day);
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
                        + " VALUES (?, ?)",text, day);
                System.out.println("0"+ LocalDate.now());
                break;
            case 1:
                jdbc.update("UPDATE comment" 
                        + " SET text = ?"
                        + "WHERE day = ?",text, day);
                System.out.println("1"+ LocalDate.now());
                break;
        }
    }



}
