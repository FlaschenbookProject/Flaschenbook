package com.book.flaschenbook.model;

import lombok.Data;

import java.util.List;

@Data
public class SurveyContentModel {

    private String id;
    private String content;
    private String type;
    private List<String> isbn;

}
