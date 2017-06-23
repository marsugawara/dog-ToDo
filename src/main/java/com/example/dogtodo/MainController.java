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
    
    @Autowired
    private CommentDao commentDao;

    // 処理の中身
    @GetMapping("/dogtodo") // 全体の初期ページ
    public String hello(String date, Goods goods, Model model) {
        LocalDate localDate = LocalDate.now();

        return "redirect:/dogtodo/" + localDate;
    }
    
    @GetMapping("/dogtodo/{date}")
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

        return "dogtodo";
    }

    @PostMapping("/yesterday")
    public String yesterdays(RedirectAttributes attr){
        LocalDate day = LocalDate.now().minusDays(1);

        attr.addFlashAttribute("date", day);
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/today")
    public String todays(RedirectAttributes attr){
        LocalDate day = LocalDate.now();

        attr.addFlashAttribute("date", day);
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/tomorrow")
    public String tomorrow(RedirectAttributes attr){
        LocalDate day = LocalDate.now().plusDays(1);

        attr.addFlashAttribute("date", day);
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/calendar")
    public String calendarSelect(String num01, RedirectAttributes attr){
        LocalDate day = LocalDate.parse(num01);

        attr.addFlashAttribute("date", day);
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/rokuPlus:{date}") // ろくた追加ボタン
    public String rokuplus(@PathVariable("date") String date, String item, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }
        addDay(0, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/nanaPlus:{date}") // ななこ追加ボタン
    public String nanaplus(@PathVariable("date") String date, String item, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }
        addDay(1, item, day);
        attr.addFlashAttribute(item);
        
        return "redirect:/dogtodo/" + day;
    }

    @PostMapping("/ro_afterCheck:{date}")// ろくたチェック後
    public String selectCheck0(@PathVariable("date") String date, int[] rokuClick, RedirectAttributes attr){
        LocalDateTime time = LocalDateTime.now();
        for(int i=0; i<rokuClick.length; i++){
            addChecktime(date, time, rokuClick[i]);
        }

        return "redirect:/dogtodo/" + date;
    }

    @PostMapping("/na_afterCheck:{date}")// ななこチェック後
    public String selectCheck1(@PathVariable("date") String date, int[] nanaClick, RedirectAttributes attr){
        LocalDateTime time = LocalDateTime.now();
        for(int i=0;i<nanaClick.length;i++){
            addChecktime(date, time, nanaClick[i]);
        }

        return "redirect:/dogtodo/" + date;
    }

    // チックされた時間の追加
      public void addChecktime(@PathVariable("date") String date, LocalDateTime time, int id){
        
        boolean flag = true;
        if(scheduleDao.checkFlag(id).get(0).getChecktime() != null){
            flag = false;
        }
        if(flag){
            switch(id){
                case 1:
                    scheduleDao.checkInsert(DogType.ROKUTA, Title.BREAKFAST, date, time);
                    break;
                case 2:
                    scheduleDao.checkInsert(DogType.ROKUTA, Title.DINNER, date, time);
                    break;
                case 3:
                    scheduleDao.checkInsert(DogType.NANAKO, Title.BREAKFAST, date, time);
                    break;
                case 4:
                    scheduleDao.checkInsert(DogType.NANAKO, Title.DINNER, date, time);
                    break;
    
                default:
                    scheduleDao.checkUpdate(time, id);
            }
        }
    }

    // チェックリストの作成
    private List<Goods> getGoods(int dogType, LocalDate date) {
        String checktime;
        List<Goods> goods = new ArrayList<Goods>();
        List<Schedule> schedules = new ArrayList<>();
        if(scheduleDao.findScheduleByDogTypeDayAndTitle(DogType.of(dogType), date, Title.BREAKFAST).size() == 0){
            schedules.add(scheduleDao.findScheduleByBreakfastId(DogType.of(dogType)));
        } else{
            schedules.addAll(scheduleDao.findScheduleByDogTypeDayAndTitle(DogType.of(dogType), date, Title.BREAKFAST));
        }
        if(scheduleDao.findScheduleByDogTypeDayAndTitle(DogType.of(dogType), date, Title.DINNER).size() == 0){
            schedules.add(scheduleDao.findScheduleByDinnerId(DogType.of(dogType)));
        } else{
            schedules.addAll(scheduleDao.findScheduleByDogTypeDayAndTitle(DogType.of(dogType), date, Title.DINNER));
        }
        
        schedules.addAll(scheduleDao.findScheduleByDogTypeAndDay(DogType.of(dogType), date, Title.BREAKFAST, Title.DINNER));
        
        for (Schedule schedule : schedules) {
            if ((schedule).getChecktime() == null) {
                checktime = "";
            } else {
                //時分秒のみ表示する
                checktime = "(" + schedule.getChecktime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) + ")";
            }
            goods.add(new Goods((schedule).getTitle().toString(), checktime, (schedule).getId()));
        }

        return goods;
    }

    // 基本項目の追加
    public void addDay(int dogType, String title, LocalDate day) {
        scheduleDao.addDay(dogType, title, day);
    }

    @PostMapping("/comment/{date}") // コメント書込ボタン
    public String sample(CommentForm form, @PathVariable("date") String date, RedirectAttributes attr) {
        LocalDate day = LocalDate.now();
        if(date.equals(null) == false && date.equals("") == false){
            day = LocalDate.parse(date);
        }

        addComment(day, form.getComm());
        attr.addFlashAttribute(form.getComm());

        return "redirect:/dogtodo/" + day;
    }

    //コメント表示
    public void printComment(LocalDate day, Model model){
        String textcomm = "";
        
        List<Comment>text_comment = commentDao.findCommentDay(day);
        if(text_comment.size() != 0){
            textcomm = (text_comment.get(0)).getText().toString();
        }
        model.addAttribute("comm",textcomm);
        
    }

    // コメントの追加
    public void addComment(LocalDate day, String text){
        List<Comment> comm_daySQL = commentDao.findComment();
        
        int k = 0;
        for (int i = 0; i < comm_daySQL.size(); i++){
            LocalDate date = LocalDate.parse((comm_daySQL.get(i)).getDay().toString());
            if (date.equals(day)) {
                k = 1;
                break;
            }else{
                k = 0;
            }
        }
        switch(k){
            case 0:
                commentDao.commInsert(text, day);
                break;
            case 1:
                commentDao.commUpdate(text, day);
                break;
        }
    }



}
