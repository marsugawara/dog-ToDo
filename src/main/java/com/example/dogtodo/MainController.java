package com.example.dogtodo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    // 処理の中身
    @GetMapping("/indent") // 全体の初期ページ
    public String hello(String date, Goods goods, Model model) {
        LocalDate localDate = LocalDate.now();

        return "redirect:/indent:" + localDate;
    }
    
    @GetMapping("/indent:{date}")
    public String helloworld(@PathVariable("date") String date, Model model){
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

    @PostMapping("/rokuPlus:{date}") // ろくた追加ボタン
    public String rokuplus(@PathVariable("date") String date, String item, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }
        addDay(0, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/indent:" + day;
    }

    @PostMapping("/nanaPlus:{date}") // ななこ追加ボタン
    public String nanaplus(@PathVariable("date") String date, String item, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }
        addDay(1, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/indent:" + day;
    }

    @PostMapping("/ro_afterCheck:{date}")// ろくたチェック後
    public String selectCheck0(@PathVariable("date") String date, int[] rokuClick, RedirectAttributes attr){
        LocalDateTime time = LocalDateTime.now();
        for(int i=0; i<rokuClick.length; i++){
            addChecktime(time, rokuClick[i]);
        }

        return "redirect:/indent:" + date;
    }

    @PostMapping("/na_afterCheck:{date}")// ななこチェック後
    public String selectCheck1(@PathVariable("date") String date, int[] nanaClick, RedirectAttributes attr){
        LocalDateTime time = LocalDateTime.now();
        for(int i=0;i<nanaClick.length;i++){
            addChecktime(time, nanaClick[i]);
        }

        return "redirect:/indent:" + date;
    }

    // チックされた時間の追加
      public void addChecktime(LocalDateTime time, int id){
        
        boolean flag = true;
        if(jdbc.queryForList("SELECT checktime FROM schedule WHERE id = ?", id).get(0).get("checktime") != null){
            flag = false;
        }
        if(flag){
        jdbc.update("UPDATE schedule "
                + "SET checktime = ?"
                + "WHERE id = ?", time, id);
        }
    }

    // チェックリストの作成
    private List<Goods> getGoods(int dogType, LocalDate date) {
        String checktime;
        List<Goods> goods = new ArrayList<Goods>();
        List<Map<String, Object>> list;
        List<Map<String, Object>> schedules;
        list = jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title = ?", dogType, date, "朝ごはん");
        if(list.size() == 0){
            schedules = jdbc.queryForList("SELECT * FROM schedule WHERE id = ?", dogType*2 + 1);    //追加時INSERT
        } else{
            schedules = jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title = ?", dogType, date, "朝ごはん");
        }
        
        list = jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title = ?", dogType, date, "夜ごはん");
        if(list.size() == 0){
            schedules.addAll(jdbc.queryForList("SELECT * FROM schedule WHERE id = ?", (dogType+1)*2));    //追加時INSERT
        } else{
            schedules.addAll(jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ? AND title = ?", dogType, date, "夜ごはん"));
        }
        
        schedules.addAll(jdbc.queryForList("SELECT * FROM schedule WHERE dogtype = ? AND day = ?", dogType, date));

        for (Map<String, Object> schedule : schedules) {
            if ((schedule).get("checktime") == null) {
                checktime = "";
            } else {
                //時分秒のみ表示する
                checktime = (schedule).get("checktime").toString();
                checktime = checktime.split(" ")[1];
                checktime = checktime.substring(0, checktime.indexOf("."));
            }
            goods.add(new Goods((schedule).get("title").toString(), checktime, Integer.parseInt((schedule).get("id").toString())));
        }

        return goods;
    }

    // 基本項目の追加
    public void addDay(int dogtype, String title, LocalDate day) {
        jdbc.update("INSERT INTO schedule (dogtype, title, day)"
                + " VALUES(?, ?, ?)",dogtype,title,day);
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

    //コメント表示
    public void printComment(LocalDate day, Model model){
        String textcomm = "";
        
        List<Map<String, Object>>text_comment = jdbc.queryForList("SELECT text FROM comment WHERE day=?",day);
        if(text_comment.size() != 0){
            textcomm = (text_comment.get(0)).get("text").toString();
        }
        model.addAttribute("comm",textcomm);
        
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
                break;
            case 1:
                jdbc.update("UPDATE comment" 
                        + " SET text = ?"
                        + "WHERE day = ?",text, day);
                break;
        }
    }



}
