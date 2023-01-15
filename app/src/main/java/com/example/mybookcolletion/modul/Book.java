package com.example.mybookcolletion.modul;

import java.io.Serializable;

public class Book implements Serializable {
    public String bookName;
    public String writerName;
    public String image;

    public Book(String bookName, String writerName, String image) {
        this.bookName = bookName;
        this.writerName = writerName;


        this.image = image;
    }


}
