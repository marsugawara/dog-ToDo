package com.example.dogtodo;

import java.text.SimpleDateFormat;
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
    
    
    @GetMapping("/test")
        public String test(){
            System.out.println(jdbc.queryForList("SELECT dogtype,title FROM schedule WHERE dogtype=0"));
            System.out.println(jdbc.queryForList("SELECT dogtype,title FROM schedule WHERE dogtype=1 AND day IS null"));
            return "indent";
        }
    
//使うやつ
    private List<Goods> rokuta;
    private List<Goods> nanako;


//処理の中身    
    @GetMapping("/hello")
        public String hello(Goods goods, Model model){
            printList(model);
            return "indent";
        }
    @GetMapping("/nanaplus")
        public String nanaplus(String item, Model model){
            addDay(0, item, new Date());
            printList(model);
            model.addAttribute("item", item);
            nanako.add(new Goods(item,"50時"));
            return "indent";
        }
    
    
    @GetMapping("/form")
        public String sample(String name, Model model) {
        model.addAttribute("name", name);
        printList(model);
        return "indent";
    }


//メソッド作成    
    public void printList(Model model){         //チェックボックスのメソッド
        rokuta = new ArrayList<Goods>();        //rokutaのリスト作成
        //rokuta.add(new Goods("朝ごはん","ぬき"));
        //rokuta.add(new Goods("夜ごはん","少し"));
        List<Map<String, Object>> rokutaSQL = jdbc.queryForList("SELECT title,checktime FROM schedule WHERE dogtype=0");
        for(int i=0;i<rokutaSQL.size();i++){
            String checktime;
            if((rokutaSQL.get(i)).get("checktime") == null){
                checktime = "";
            } else{
                checktime = (rokutaSQL.get(i)).get("checktime").toString();
            }
            rokuta.add(new Goods((rokutaSQL.get(i)).get("title").toString(), checktime));
        }
        
        nanako = new ArrayList<Goods>();        //nanakoのリスト作成
        nanako.add(new Goods("朝ごはん","９時"));
        nanako.add(new Goods("夜ごはん","２０時"));
        
        model.addAttribute("rokutaGohans", rokuta); //表示するために渡してる
        model.addAttribute("nanakogohans", nanako); //表示するために渡してる
    }
    
    public void addDay(int dogType, String title, Date day){
        jdbc.update("INSERT INTO schedule (dogtype, title, day) VALUES (" + dogType + ", '" + title + "', '" + new SimpleDateFormat("yyyy/MM/dd").format(day) + "')");
    }
}
