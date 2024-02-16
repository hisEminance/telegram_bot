package com.example.tgbot.dto;

import com.opencsv.bean.CsvBindByName;

public class Vacancydto {
    @CsvBindByName(column = "Id")
    private String id;
    @CsvBindByName(column = "Title")

    private String title;
    @CsvBindByName(column = "Short description")

    private String Shortdescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortdescription() {
        return Shortdescription;
    }

    public void setShortdescription(String shortdescription) {
        Shortdescription = shortdescription;
    }
}
