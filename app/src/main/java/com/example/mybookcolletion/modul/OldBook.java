package com.example.mybookcolletion.modul;

import java.io.Serializable;

public class OldBook implements Serializable {

    public String image;
    public String bookTxt;
    public String writerTxt;
    public String excreptTxt;

    public OldBook(String image,String bookTxt,String writerTxt,String excreptTxt){
        this.image=image;
        this.bookTxt=bookTxt;
        this.writerTxt=writerTxt;
        this.excreptTxt=excreptTxt;

    }



}
