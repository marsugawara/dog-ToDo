package com.example.dogtodo;

public class Goods {
    private int id;
    private String title;
    private String chtime;
    
    //コンストラクタ
    public Goods(){}
    
    public Goods (String title, String chtime, int id){
        this.title = title;
        this.chtime = chtime;
        this.id = id;
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
    //チェックID：セッター・ゲッター
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
}
