package com.example.dogtodo;

public class Goods {
    private String title;
    private String chtime;
    
    //コンストラクタ
    public Goods(){}
    
    public Goods (String title, String chtime){
        this.title = title;
        this.chtime = chtime;
    }
    
    //表示メソッド
    public void printCheck(){
        System.out.println("項目：" +title +"時間：" +chtime);
    }
    
    
    //チェック項目名：ゲッター・セッター
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    //チェック日時：セッター・ゲッター
    public void setChtime(String chtime){
        this.chtime = chtime;
    }
    public String getChtime(){
        return chtime;
    }
}
